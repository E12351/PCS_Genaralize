package lk.dialog.iot.pcs.service;

import java.util.Map;

import lk.dialog.iot.pcs.exception.impl.PluginBehaviorException;
import ro.fortsoft.pf4j.ExtensionPoint;

public interface PluginBehavior extends ExtensionPoint {

    Map<String, ?> pluginOperation(Map<String, Object> receivedMap) throws PluginBehaviorException;

}
