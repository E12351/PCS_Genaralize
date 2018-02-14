package lk.dialog.iot.pcs.service;

import java.util.Collection;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import lk.dialog.iot.pcs.dto.PluginDto;
import lk.dialog.iot.pcs.exception.impl.PluginBehaviorException;
import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;
import lk.dialog.iot.pcs.model.Plugin;

public interface PluginService {

    Collection<Plugin> findAll();

    Plugin findOne(int id);

    Plugin create(Plugin plugin) throws ProtocolConverterException;

    Plugin update(int id, Plugin plugin) throws ProtocolConverterException;

    void delete(int id);

    Plugin getPluginByEndPoint(String endPoint);

    Map<String, ?> executePluginOperation(Map<String, Object> receivedMap) throws ProtocolConverterException;
    
    boolean uploadPluginFile(MultipartFile pluginFile) throws ProtocolConverterException;

    Plugin getPluginByDeviceDefinitionId(int deviceDefinitionId);
    
    PluginDto fromPluginToPluginDto(Plugin plugin);
    
    Plugin fromPluginDtoToPlugin(PluginDto pluginDto);

    boolean deletePluginFile(String pluginName) throws ProtocolConverterException;
}
