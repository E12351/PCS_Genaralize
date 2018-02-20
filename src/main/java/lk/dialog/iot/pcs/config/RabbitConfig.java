package lk.dialog.iot.pcs.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

import lk.dialog.iot.pcs.service.ConsumerService;
import lk.dialog.iot.pcs.util.Constants;

@Configuration
public class RabbitConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${broker.host}")
    private String host;

    @Value("${broker.mqtt.port}")
    private int mqttPort;

    @Value("${broker.amqp.port}")
    private int amqpPort;

    @Value("${broker.username}")
    private String username;

    @Value("${broker.password}")
    private String password;

    @Value("${broker.exchangeName}")
    private String exchangeName;

    @Value("${consumer.queueName}")
    private String consumerQueueName;

    @Value("${consumer.topicName}")
    private String consumerTopicName;

    @Value("${consumer.numOfConsumers}")
    private int numOfConsumers;

    @Value("${mqtt.client.id}")
    private String mqttClientId;

    @Autowired
    private ConsumerService consumerService;

    @Bean
    public TopicExchange topic() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue consumerQueue() {
        return new Queue(consumerQueueName, false);
    }

    @Bean
    public Binding bindingConsumerQueue(TopicExchange topic, Queue consumerQueue) {
        return BindingBuilder.bind(consumerQueue).to(topic).with(consumerTopicName);
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs("tcp://" + host + ":" + mqttPort);
        factory.setUserName(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(@Qualifier("mqttClientFactory") MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(mqttClientId, mqttClientFactory);
        messageHandler.setAsync(true);
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel amqpInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public AmqpInboundChannelAdapter inbound(
            @Qualifier("rabbitMessageListnerContainer") SimpleMessageListenerContainer rabbitMessageListnerContainer,
            @Qualifier("amqpInputChannel") MessageChannel amqpInputChannel) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(rabbitMessageListnerContainer);
        adapter.setOutputChannel(amqpInputChannel);
        return adapter;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host, amqpPort);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        return cachingConnectionFactory;
    }

    @Bean
    public SimpleMessageListenerContainer rabbitMessageListnerContainer(@Qualifier("cachingConnectionFactory") CachingConnectionFactory cachingConnectionFactory) {
        SimpleMessageListenerContainer rabbitMessageListnerContainer = new SimpleMessageListenerContainer( cachingConnectionFactory);
        rabbitMessageListnerContainer.setQueueNames(consumerQueueName);
        rabbitMessageListnerContainer.setConcurrentConsumers(numOfConsumers);
        return rabbitMessageListnerContainer;
    }

    @Bean
    @ServiceActivator(inputChannel = "amqpInputChannel")
    public MessageHandler rabbitMessageHandler() {
        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                if (message != null) {
                    Object messageObject = message.getPayload();
                    MessageHeaders messageHeaders = message.getHeaders();

                    if (messageObject != null && messageHeaders != null) {
                        try {
                            String payload = new String((byte[]) messageObject);
                            String subscribeTopic = (String) messageHeaders.get(Constants.AMQP_HEADER_TOPIC_KEY);

                            if (subscribeTopic != null) {

                                MDC.put(Constants.LOG_IDENTIFIER_KEY, payload.substring(payload.length() - Constants.LOG_IDENTIFIER_LENGTH));
//                                payload = payload.substring(0, payload.length() - Constants.LOG_IDENTIFIER_LENGTH);

//                                logger.info("Subscribe topic : {}, message: {}.", subscribeTopic, payload);
                                consumerService.callExternalService(subscribeTopic, payload);

                            } else {
                                logger.info("Null message topic received");
                            }

                        } catch (ClassCastException e) {
                            logger.error("Subscribe parameters casting exception : {}.", e.getMessage());
                        }
                    } else {
                        logger.info("Null message parameters subscribed");
                    }
                } else {
                    logger.info("Null message subscribed");
                }
            }
        };
    }
}
