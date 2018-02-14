package lk.dialog.iot.pcs.service.impl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import lk.dialog.iot.pcs.service.MttqPublisherService;
import lk.dialog.iot.pcs.service.PluginDbService;

@Component
public class PluginServiceProvider implements ApplicationContextAware {

    private static PluginServiceProvider pluginServiceProvider;

    @Autowired
    private PluginDbService pluginDbService;

    @Autowired
    @Qualifier("taskScheduler")
    private TaskScheduler scheduler;

    @Autowired
    private MttqPublisherService mttqPublisherService;

    private PluginServiceProvider() {
    }

    public static PluginServiceProvider getPluginServiceProvider() {
        if (pluginServiceProvider == null) {
            synchronized (PluginServiceProvider.class) {
                if (pluginServiceProvider == null) {
                    pluginServiceProvider = new PluginServiceProvider();
                }
            }
        }
        return pluginServiceProvider;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        pluginServiceProvider  = this;
    }

    public MttqPublisherService getMttqPublisherService() {
        return mttqPublisherService;
    }

    public TaskScheduler getScheduler() {
        return scheduler;
    }

    public PluginDbService getPluginDbService() {
        return pluginDbService;
    }
}
