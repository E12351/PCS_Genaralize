package lk.dialog.iot.pcs.service;

import lk.dialog.iot.pcs.dto.PluginDto;

public interface PluginDbService {

    PluginDto getPluginDtoByEndPoint(String endPoint);
}
