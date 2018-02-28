package lk.dialog.iot.pcs.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lk.dialog.iot.pcs.dto.MqttPublishDto;
import lk.dialog.iot.pcs.dto.PluginDto;
import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;
import lk.dialog.iot.pcs.model.Plugin;
import lk.dialog.iot.pcs.service.HttpEndpointService;
import lk.dialog.iot.pcs.service.MttqPublisherService;
import lk.dialog.iot.pcs.service.PluginService;
import lk.dialog.iot.pcs.util.Constants;
import lk.dialog.iot.pcs.util.EnumTypes;

@Service
public class HttpEndpointServiceImpl implements HttpEndpointService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean isDebudEnable = logger.isDebugEnabled();

    @Autowired
    private MttqPublisherService mttqPublisherService;

    @Autowired
    private PluginService pluginService;

    @Value("${topic.prefix}")
    private String prefix;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void publishToBroker(String endpoint, Object messageObject) throws ProtocolConverterException {

        Plugin plugin = pluginService.getPluginByEndPoint(endpoint);

        if (plugin != null) {

            PluginDto pluginDto = pluginService.fromPluginToPluginDto(plugin);
            if (pluginDto == null) {
                logger.info("Null pluginDto mapped for endpoint : {}.", endpoint);
                throw new ProtocolConverterException("Null pluginDto mapped for endpoint : " + endpoint);
            }
//
            Map<String, Object> pluginSendingMap = new HashMap<String, Object>();
            pluginSendingMap.put(Constants.OPERATION_KEY, EnumTypes.OperationTypes.HTTP_TO_BROKER_OPERATION.getName());
            pluginSendingMap.put(Constants.PLUGIN_NAME_KEY, plugin.getPlugunName());
            pluginSendingMap.put(Constants.MESSAGE_OBJECT_KEY, messageObject);
            pluginSendingMap.put(Constants.PLUGIN_DTO_KEY, pluginDto);
            try {
                pluginService.executePluginOperation(pluginSendingMap);
            } catch (ProtocolConverterException e) {
                logger.error("Plugin http request execution exception : {}.", e.getMessage());
                throw new ProtocolConverterException("Http request execution exception : " + e.getMessage());
//            }
        }
//        else {
//
//            try {
//                MqttPublishDto mqttPublishDto = new MqttPublishDto();
//                mqttPublishDto.setTopic(endpoint + "/" + prefix);
//                mqttPublishDto.setMessage(objectMapper.writeValueAsString(messageObject));
//                mttqPublisherService.publishMessage(mqttPublishDto);
//                if (isDebudEnable) {
//                    logger.debug("No plugin found for endpoint : {}, and publishing to common.", endpoint);
//                }
//            } catch (JsonProcessingException e) {
//                logger.error("Json convertion exception : {}.", e.getMessage());
//                throw new ProtocolConverterException("Http request execution exception : " + e.getMessage());
//            }
        }
    }
}
