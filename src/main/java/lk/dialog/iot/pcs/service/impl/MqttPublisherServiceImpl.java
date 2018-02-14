package lk.dialog.iot.pcs.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import lk.dialog.iot.pcs.dto.MqttPublishDto;
import lk.dialog.iot.pcs.service.MttqPublisherService;
import lk.dialog.iot.pcs.util.Constants;

@Service
public class MqttPublisherServiceImpl implements MttqPublisherService,  ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MyGateway gateway;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        gateway = context.getBean(MyGateway.class);
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway {
        void sendToMqtt(Message<String> data);
    }

    @Override
    public void publishMessage(MqttPublishDto mqttPublishDto) {
        if (mqttPublishDto != null && mqttPublishDto.getMessage() != null && mqttPublishDto.getTopic() != null) {

            mqttPublishDto.setMessage(mqttPublishDto.getMessage() + MDC.get(Constants.LOG_IDENTIFIER_KEY));
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Publish topic : {}, message : {}.", mqttPublishDto.getTopic(), mqttPublishDto.getMessage());
                }
                
                gateway.sendToMqtt(MessageBuilder.withPayload(mqttPublishDto.getMessage()).setHeader(MqttHeaders.TOPIC, mqttPublishDto.getTopic()).build());
            } catch (IllegalArgumentException ex) {
                logger.error("Message publish exception {}.", ex.getMessage());
            }
        } else {
            logger.info("Invalid mqtt publishing request");
        }
    }
}