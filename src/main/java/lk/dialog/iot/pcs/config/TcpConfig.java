//package lk.dialog.iot.pcs.config;
//
//import java.util.UUID;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
//import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
//import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.messaging.MessagingException;
//
//import lk.dialog.iot.pcs.dto.MqttPublishDto;
//import lk.dialog.iot.pcs.service.MttqPublisherService;
//import lk.dialog.iot.pcs.util.Constants;
//
//@Configuration
//public class TcpConfig {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private MttqPublisherService mttqPublisherService;
//
//    @Value("${tcp.port}")
//    private int tcpPort;
//
//    @Value("${tcp.topic.publish}")
//    private String tcpPublishTopic;
//
//    @Bean
//    public TcpNetServerConnectionFactory tcpConnFactory() {
//        return new TcpNetServerConnectionFactory(tcpPort);
//    }
//
//    @Bean
//    public TcpReceivingChannelAdapter tcpReceivingChannelAdapter(@Qualifier("tcpConnFactory") AbstractServerConnectionFactory tcpConnFactory, @Qualifier("tcpInputChannel") MessageChannel tcpInputChannel) {
//        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
//        adapter.setConnectionFactory(tcpConnFactory);
//        adapter.setOutputChannel(tcpInputChannel);
//        return adapter;
//    }
//
//    @Bean
//    public MessageChannel tcpInputChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "tcpInputChannel")
//    public MessageHandler tcpMessageHandler() {
//
//        return new MessageHandler() {
//
//            @Override
//            public void handleMessage(Message<?> message) throws MessagingException {
//
//                MDC.put(Constants.LOG_IDENTIFIER_KEY, UUID.randomUUID().toString());
//
//                if (message != null) {
//                    Object messageObject = message.getPayload();
//                    MessageHeaders messageHeaders = message.getHeaders();
//
//                    if (messageObject != null && messageHeaders != null) {
//                        try {
//                            String payload = new String((byte[]) messageObject);
//                            logger.info("Tcp received message : {}", payload);
//
//                            if (!"".equals(payload.trim())) {
//                                mttqPublisherService.publishMessage(new MqttPublishDto(tcpPublishTopic, payload));
//                            }
//
//                        } catch (ClassCastException e) {
//                            logger.error("Tcp received parameters casting exception : {}, message : {}.", e.getMessage(), messageObject);
//                        }
//                    } else {
//                        logger.info("Null message parameters received via tcp");
//                    }
//                } else {
//                   logger.info("Null message received via tcp");
//                }
//            }
//        };
//    }
//}
