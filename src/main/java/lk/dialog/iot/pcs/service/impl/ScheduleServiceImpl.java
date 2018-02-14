//package lk.dialog.iot.pcs.service.impl;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import lk.dialog.iot.pcs.dto.DeviceDto;
//import lk.dialog.iot.pcs.dto.PluginDto;
//import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;
//import lk.dialog.iot.pcs.model.Plugin;
//import lk.dialog.iot.pcs.service.PluginService;
//import lk.dialog.iot.pcs.service.ScheduleService;
//import lk.dialog.iot.pcs.util.Constants;
//import lk.dialog.iot.pcs.util.EnumTypes;
//
//@Service
//public class ScheduleServiceImpl implements ScheduleService {
//
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//    private boolean isDebugEnable = logger.isDebugEnabled();
//
//    @Value("${url.deviceAdminService.device}")
//    private String dasUrl;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private PluginService pluginService;
//
//    @Override
//    public void executeSchedule(int deviceId) throws ProtocolConverterException {
//
//        DeviceDto deviceDTO = null;
//        try {
//            deviceDTO = restTemplate.getForObject(dasUrl + deviceId, DeviceDto.class);
//        } catch (RestClientException res) {
//            logger.error("Device admin service call exception for device id : {}, message : {}", deviceId,
//                    res.getMessage());
//            throw new ProtocolConverterException("Device admin service call exception for device id : " + deviceId);
//        }
//
//        if (isDebugEnable) {
//            logger.debug("Device admin service response : {}, for device id : {}.", deviceDTO, deviceId);
//        }
//
//        if (deviceDTO == null) {
//            logger.error("No device found for device id : {}.", deviceId);
//            throw new ProtocolConverterException("No device found for device id : " + deviceId);
//        }
//
//        Plugin plugin = pluginService.getPluginByDeviceDefinitionId(deviceDTO.getDeviceDefinitionId());
//        if (plugin == null) {
//            logger.error("No plugin found for device definition id : {}.", deviceDTO.getDeviceDefinitionId());
//            throw new ProtocolConverterException("No plugin found for device id : " + deviceId);
//        }
//
//        if (isDebugEnable) {
//            logger.debug("Plugin found for device definition id : {}.", deviceDTO.getDeviceDefinitionId());
//        }
//
//        PluginDto pluginDto = pluginService.fromPluginToPluginDto(plugin);
//        if (pluginDto == null) {
//            logger.info("Null pluginDto mapped for device id : {}", deviceId);
//            throw new ProtocolConverterException("Null pluginDto mapped for device id : " + deviceId);
//        }
//
//        Map<String, Object> pluginScheduleMap = new HashMap<String, Object>();
//        pluginScheduleMap.put(Constants.OPERATION_KEY, EnumTypes.OperationTypes.SCHEDULE__OPERATION.getName());
//        pluginScheduleMap.put(Constants.PLUGIN_NAME_KEY, pluginDto.getPlugunName());
//        pluginScheduleMap.put(Constants.PLUGIN_DTO_KEY, pluginDto);
//        pluginScheduleMap.put(Constants.DEVICE_DTO_KEY, deviceDTO);
//
//        try {
//            pluginService.executePluginOperation(pluginScheduleMap);
//        } catch (ProtocolConverterException e) {
//            logger.error("Plugin schedule execution exception : {}.", e.getMessage());
//            throw new ProtocolConverterException("Schedule execution exception : " + e.getMessage());
//        }
//
//        if (isDebugEnable) {
//            logger.debug("Plugin schedule executed successfully for device id : {}.", deviceId);
//        }
//    }
//}
