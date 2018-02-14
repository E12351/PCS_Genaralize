package lk.dialog.iot.pcs.service;

import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;

public interface HttpEndpointService {

    void publishToBroker(String endpoint, Object messageObject) throws ProtocolConverterException;
}
