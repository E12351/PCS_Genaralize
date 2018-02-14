package lk.dialog.iot.pcs.service;

import lk.dialog.iot.pcs.dto.MqttPublishDto;

public interface MttqPublisherService {

    void publishMessage(MqttPublishDto mqttPublishDto);
}
