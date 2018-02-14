package lk.dialog.iot.pcs.service;

public interface ConsumerService {

    void callExternalService(String topic, String message);
}
