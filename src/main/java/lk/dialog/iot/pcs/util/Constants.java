package lk.dialog.iot.pcs.util;

public class Constants {

    public static final String OPERATION_KEY = "operation";
    public static final String END_POINT_KEY = "endpoint";
    public static final String PLUGIN_DTO_KEY = "pluginDto";
    public static final String MESSAGE_OBJECT_KEY = "messageObject";
    public static final String TOPIC_KEY = "topic";
    public static final String PLUGIN_RESPONSE_KEY = "pluginResponse";
    public static final String PLUGIN_NAME_KEY = "pluginName";
    
    public static final String SUCCESS_MESSAGE = "{\"status\": \"success\"}";
    public static final String FAIL_MESSAGE = "{\"status\": \"Failed\"}";
    public static final String DEVICE_DTO_KEY = "deviceDto";
    
    public static final String AMQP_HEADER_TOPIC_KEY = "amqp_receivedRoutingKey";

    public static final String LOG_IDENTIFIER_KEY = "UUID";
    public static final int LOG_IDENTIFIER_LENGTH = 36;
    public static final String PLUGIN_CONTENT_TYPE = "application/zip";
}
