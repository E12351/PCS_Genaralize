package lk.dialog.iot.pcs.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lk.dialog.iot.pcs.dto.PluginDto;
import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;
import lk.dialog.iot.pcs.model.Plugin;
import lk.dialog.iot.pcs.service.ConsumerService;
import lk.dialog.iot.pcs.service.PluginService;
import lk.dialog.iot.pcs.util.Constants;
import lk.dialog.iot.pcs.util.EnumTypes;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PluginService pluginService;

    @Value("${topic.prefix}")
    private String prefix;

    @Override
    public void callExternalService(String topic, String message) {

        Plugin plugin = null;

        Collection<Plugin> plugins = pluginService.findAll();
        if (plugins != null && !(plugins.isEmpty())) {
            Iterator<Plugin> itrPlugins = plugins.iterator();
            while (itrPlugins.hasNext()) {
                Plugin itrPlugin = itrPlugins.next();
                if (topic.matches("^.+" + itrPlugin.getActionSubscribeTopicRegex() + "$")) {
                    plugin = itrPlugin;
                    break;
                }
            }
        } else {
            logger.info("Null pcs plugin records");
        }

        if (plugin != null) {

            String pluginName = plugin.getPlugunName();
            
            PluginDto pluginDto = pluginService.fromPluginToPluginDto(plugin);
            if (pluginDto != null) {

                Map<String, Object> pluginSendingMap = new HashMap<String, Object>();
                pluginSendingMap.put(Constants.OPERATION_KEY, EnumTypes.OperationTypes.TO_EXTERNAL_OPERATION.getName());
                pluginSendingMap.put(Constants.PLUGIN_NAME_KEY, pluginName);
                pluginSendingMap.put(Constants.MESSAGE_OBJECT_KEY, message);
                pluginSendingMap.put(Constants.PLUGIN_DTO_KEY, pluginDto);
                pluginSendingMap.put(Constants.TOPIC_KEY, topic);

                try {
                    pluginService.executePluginOperation(pluginSendingMap);
                } catch (ProtocolConverterException e) {
                    logger.error("Plugin service call exception : {}.", e.getMessage());
                }
            } else {
                logger.error("Plugin mapped to null pluginDto");
            }
        } else {
            logger.warn("No plugin found for subscribed topic : {}.", topic);
        }
    }
}
