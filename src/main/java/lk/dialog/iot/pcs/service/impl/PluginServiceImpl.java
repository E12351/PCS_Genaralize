package lk.dialog.iot.pcs.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import lk.dialog.iot.pcs.dto.PluginDto;
import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;
import lk.dialog.iot.pcs.model.Plugin;
import lk.dialog.iot.pcs.repository.PluginRepository;
import lk.dialog.iot.pcs.service.PluginBehavior;
import lk.dialog.iot.pcs.service.PluginDbService;
import lk.dialog.iot.pcs.service.PluginService;
import lk.dialog.iot.pcs.util.Constants;
import ro.fortsoft.pf4j.PluginManager;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class PluginServiceImpl implements PluginService, PluginDbService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean isDebugEnable = logger.isDebugEnabled();

    @Autowired
    private PluginRepository pluginRepository;

    @Autowired
    private PluginManager pluginManager;

    @Autowired
    private Map<String, PluginBehavior> pluginMap;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${dir.plugins}")
    private String dirPlugins;

    @Value("${plugin.name.regex}")
    private String pluginNameRegex;

    @Override
    @Cacheable(value = "pcs_all_plugins", key = "#root.methodName")
    public Collection<Plugin> findAll() {
        return pluginRepository.findAll();
    }

    @Override
    public Plugin findOne(int id) {
        return pluginRepository.findOne(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @CacheEvict(value = "pcs_all_plugins", allEntries = true)
    @Caching(put = {
            @CachePut(value = "pcs_plugin_endpoint", key = "#result.endPoint", condition = "#result != null && #result.endPoint != null"),
            @CachePut(value = "pcs_plugin_devicedefinition", key = "#result.deviceDefinitionId", condition = "#result != null && #result.deviceDefinitionId > 0") })
    public Plugin create(Plugin plugin) throws ProtocolConverterException {
        if (plugin.getId() != 0) {
            throw new ProtocolConverterException("Plugin id has set with insertion");
        }
        return pluginRepository.save(plugin);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @CacheEvict(value = { "pcs_all_plugins", "pcs_plugin_endpoint", "pcs_plugin_devicedefinition" }, allEntries = true)
    public Plugin update(int id, Plugin plugin) throws ProtocolConverterException {
        Plugin persistedPlugin = findOne(id);
        if (persistedPlugin == null) {
            throw new ProtocolConverterException("No plugin found for update with pluginId : " + id);
        }
        plugin.setId(id);
        return pluginRepository.save(plugin);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @CacheEvict(value = { "pcs_all_plugins", "pcs_plugin_endpoint", "pcs_plugin_devicedefinition" }, allEntries = true)
    public void delete(int id) {
        pluginRepository.delete(id);
    }

    @Override
    @Cacheable(value = "pcs_plugin_endpoint", key = "#endPoint")
    public Plugin getPluginByEndPoint(String endPoint) {
        return pluginRepository.findOneByEndPoint(endPoint);
    }

    @Override
    @Cacheable(value = "pcs_plugin_devicedefinition", key = "#deviceDefinitionId")
    public Plugin getPluginByDeviceDefinitionId(int deviceDefinitionId) {
        return pluginRepository.findOneByDeviceDefinitionId(deviceDefinitionId);
    }

    @Override
    public PluginDto getPluginDtoByEndPoint(String endPoint) {
        Plugin plugin = getPluginByEndPoint(endPoint);
        if (plugin == null) {
            return null;
        }
        return fromPluginToPluginDto(plugin);
    }

    @Override
    public Map<String, ?> executePluginOperation(Map<String, Object> receivedMap) throws ProtocolConverterException {

        if (isDebugEnable) {
//            logger.debug("Plugin behavior received : {}.", receivedMap);
//            logger.debug("Plugin map : {}.", pluginMap);
        }

        Map<String, ?> responseMap = null;

        if (receivedMap == null) {
            logger.info("Plugin execute operation received null requestMap: {}.", receivedMap);
            throw new ProtocolConverterException("Null map received");
        }

        String pluginName = (String) receivedMap.remove(Constants.PLUGIN_NAME_KEY);
        if (pluginName == null) {
            logger.info("Plugin name null for request : {}.", receivedMap);
            throw new ProtocolConverterException("Null plugin name");
        }

        PluginBehavior pluginBehavior = pluginMap.get(pluginName);

        if (pluginBehavior == null) {
            logger.info("No registered plugin found for plugin name : {}.", pluginName);
            throw new ProtocolConverterException("No plugin exists as : " + pluginName);
        }

        try {
            responseMap = pluginBehavior.pluginOperation(receivedMap);
        } catch (Exception e) {
            logger.error("Plugin call exception {}.", e.getMessage());
            throw new ProtocolConverterException("Plugin call exception : " + e.getMessage());
        }

        if (isDebugEnable) {
            logger.debug("Plugin service response : {}.", responseMap);
        }
        return responseMap;
    }

    private boolean reloadPluginMap() {
        boolean responseStatus = false;
        try {
            pluginManager.stopPlugins();
            pluginManager.loadPlugins();
            pluginManager.startPlugins();

            Map<String, PluginBehavior> updatedPluginMap = new HashMap<String, PluginBehavior>();

            List<PluginBehavior> pluginList = pluginManager.getExtensions(PluginBehavior.class);
            if (pluginList != null && !pluginList.isEmpty()) {
                Iterator<PluginBehavior> itrPluginService = pluginList.iterator();
                while (itrPluginService.hasNext()) {
                    PluginBehavior pluginBehavior = itrPluginService.next();
                    System.out.println();
                    updatedPluginMap.put(pluginBehavior.toString(), pluginBehavior);
                }
            }
            pluginMap = updatedPluginMap;

            responseStatus = true;
            logger.info("Pf4j runtime resolved {} plugins successfully", pluginMap.size());
            logger.info("Latest plugins : {}.", pluginMap);
        } catch (Exception ex) {
            logger.error("Exception in reloading plugins " + ex);
        }
        return responseStatus;
    }

    @Override
    public boolean uploadPluginFile(MultipartFile pluginFile) throws ProtocolConverterException {

        System.out.println("Upload function called.");

        if (pluginFile == null) {
            logger.info("No plugin file received");
            throw new ProtocolConverterException("No plugin file received");
        }

        String pluginType = pluginFile.getContentType();
//        if ((pluginType == null) || (!(pluginType.trim().equalsIgnoreCase(Constants.PLUGIN_CONTENT_TYPE)))) {
//            logger.warn("Unsupported file type as : {}.", pluginType);
//            throw new ProtocolConverterException("Unsupported file type as : " + pluginType);
//        }

        boolean status = false;
        String newPluginName = pluginFile.getOriginalFilename();
        deletePluginFile(newPluginName);

        try {
            byte[] pluginByteArr = pluginFile.getBytes();
            Path path = Paths.get(dirPlugins + "/" + newPluginName);
            Files.deleteIfExists(path);
            Files.write(path, pluginByteArr);
            status = reloadPluginMap();
        } catch (IOException e) {
            logger.error("Plugin file upload exception : {}.", e.getMessage());
        }
        System.out.println("Finished.");
        return status;
    }

    @Override
    public boolean deletePluginFile(String pluginName) throws ProtocolConverterException {

        if (pluginName == null) {
            logger.info("Null plugin name received and can not delete");
            throw new ProtocolConverterException("Null plugin name received and can not delete");
        }

        boolean status = false;
        int numOfFounds = 0, numOfDeleted = 0;

        Stream<Path> oldPluginPathStream = null;

        try {
            String updatedFileName = pluginName.trim().replaceAll(pluginNameRegex, "").trim();
            Path pluginsPath = Paths.get(dirPlugins);
            oldPluginPathStream = Files.list(pluginsPath);
            if (oldPluginPathStream != null) {

                Iterator<Path> oldPluginPathItr = oldPluginPathStream.iterator();
                while (oldPluginPathItr.hasNext()) {
                    Path oldPluginPath = oldPluginPathItr.next();
                    String oldFileName = oldPluginPath.getFileName().toString();
                    String oldUpdatedFileName = oldFileName.trim().replaceAll(pluginNameRegex, "").trim();
                    if (updatedFileName.equalsIgnoreCase(oldUpdatedFileName)) {
                        boolean st = FileSystemUtils.deleteRecursively(oldPluginPath.toFile());
                        logger.debug("Deletion of old file name : {}, status : {}.", oldFileName, st);
                        numOfFounds++;
                        if (st) {
                            numOfDeleted++;
                        }
                    }
                }
            } else {
                logger.info("Null stream for plugins folder");
            }
        } catch (IOException ex) {
            logger.error("Plugin file upload exception : {}.", ex.getMessage());
        } finally {
            if (oldPluginPathStream != null) {
                oldPluginPathStream.close();
            }
        }
        if (numOfFounds > 0 && numOfFounds == numOfDeleted) {
            status = true;
        }
        return status;
    }

    @Override
    public PluginDto fromPluginToPluginDto(Plugin plugin) {

        PluginDto pluginDto = null;
        try {
            pluginDto = modelMapper.map(plugin, PluginDto.class);
        } catch (MappingException ex) {
            logger.error("Plugin to pluginDto mapping exception : {}.", ex.getMessage());
        }
        return pluginDto;
    }

    @Override
    public Plugin fromPluginDtoToPlugin(PluginDto pluginDto) {

        Plugin plugin = null;
        try {
            plugin = modelMapper.map(pluginDto, Plugin.class);
        } catch (MappingException ex) {
            logger.error("PluginDto to plugin mapping exception : {}.", ex.getMessage());
        }
        return plugin;
    }
}
