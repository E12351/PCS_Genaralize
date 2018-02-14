package lk.dialog.iot.pcs.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "plugin")
@SQLDelete(sql = "UPDATE plugin SET deleted = 1 WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = 0")
public class Plugin implements Serializable {

    private static final long serialVersionUID = -5934324278179912104L;

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String endPoint;

    private String eventMessageRegex;

    @Column(nullable = false)
    private String eventPublishTopic;

    private String actionPublishTopic;
    private String actionSubscribeTopicRegex;
    private String actionUrl;
    private int deviceDefinitionId;

    @Column(nullable = false)
    private String plugunName;

    private String parameters;

    private int deleted = 0;

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

    public String getEventMessageRegex() {
        return eventMessageRegex;
    }

    public void setEventMessageRegex(String eventMessageRegex) {
        this.eventMessageRegex = eventMessageRegex;
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
