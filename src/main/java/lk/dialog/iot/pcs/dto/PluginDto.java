package lk.dialog.iot.pcs.dto;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public class PluginDto {

    private int id;

    private String endPoint;
    private String eventMessageRegex;

    @Pattern(regexp = "^(?![/])[a-zA-Z0-9/]+/common$", message = "Please enter vaid event publish topic.")
    private String eventPublishTopic;

    @Pattern(regexp = "^(?![.])[a-zA-Z0-9/]+/pub$", message = "Please enter vaid action publish topic.")
    private String actionPublishTopic;

    @Pattern(regexp = "^(?![/])[a-zA-Z0-9.]+.sub$", message = "Please enter vaid action subscribe topic.")
    private String actionSubscribeTopicRegex;

    @URL(message = "Please enter valid url")
    private String actionUrl;

    @NotEmpty(message = "Please enter plugin name.")
    private String plugunName;

    private String parameters;

    private int deviceDefinitionId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getEventMessageRegex() {
        return eventMessageRegex;
    }

    public void setEventMessageRegex(String eventMessageRegex) {
        this.eventMessageRegex = eventMessageRegex;
    }

    public String getEventPublishTopic() {
        return eventPublishTopic;
    }

    public void setEventPublishTopic(String eventPublishTopic) {
        this.eventPublishTopic = eventPublishTopic;
    }

    public String getActionPublishTopic() {
        return actionPublishTopic;
    }

    public void setActionPublishTopic(String actionPublishTopic) {
        this.actionPublishTopic = actionPublishTopic;
    }

    public String getActionSubscribeTopicRegex() {
        return actionSubscribeTopicRegex;
    }

    public void setActionSubscribeTopicRegex(String actionSubscribeTopicRegex) {
        this.actionSubscribeTopicRegex = actionSubscribeTopicRegex;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getPlugunName() {
        return plugunName;
    }

    public void setPlugunName(String plugunName) {
        this.plugunName = plugunName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public int getDeviceDefinitionId() {
        return deviceDefinitionId;
    }

    public void setDeviceDefinitionId(int deviceDefinitionId) {
        this.deviceDefinitionId = deviceDefinitionId;
    }
}
