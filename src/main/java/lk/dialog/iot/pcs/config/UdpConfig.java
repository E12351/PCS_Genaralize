package lk.dialog.iot.pcs.config;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

import lk.dialog.iot.pcs.dto.MqttPublishDto;
import lk.dialog.iot.pcs.service.MttqPublisherService;
import lk.dialog.iot.pcs.util.Constants;

@Configuration
public class UdpConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${udp.port}")
    private int udpPort;

    @Value("${udp.topic.publish}")
    private String udpPublishTopic;

    @Autowired
    private MttqPublisherService mttqPublisherService;

    @Bean
    public MessageChannel udpInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public UnicastReceivingChannelAdapter udpInboundAdapter(
            @Qualifier("udpInputChannel") MessageChannel udpInputChannel) {
        UnicastReceivingChannelAdapter unicastReceivingChannelAdapter = new UnicastReceivingChannelAdapter(9991);
        unicastReceivingChannelAdapter.setOutputChannel(udpInputChannel);    
        return unicastReceivingChannelAdapter;
    }
    
    @Bean
    @ServiceActivator(inputChannel = "udpInputChannel")
    public MessageHandler udpMessageHandler() {

        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {

                MDC.put(Constants.LOG_IDENTIFIER_KEY, UUID.randomUUID().toString());

                if (message != null) {
                    Object messageObject = message.getPayload();
                    MessageHeaders messageHeaders = message.getHeaders();

                    if (messageObject != null && messageHeaders != null) {
                        try {
                            String payload = new String((byte[]) messageObject);
                            logger.info("Udp received message : {}", payload);

                            if (!"".equals(payload.trim())) {
                                mttqPublisherService.publishMessage(new MqttPublishDto(udpPublishTopic, payload));
                            }

                        } catch (ClassCastException e) {
                            logger.error("Udp received parameters casting exception : {}, message : {}.", e.getMessage(), messageObject);
                        }
                    } else {
                        logger.info("Null message parameters received via udp");
                    }
                } else {
                   logger.info("Null message received via udp"); 
                }
            }
        };
    }
}
