package lk.dialog.iot.pcs;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;
import lk.dialog.iot.pcs.service.ConsumerService;
import lk.dialog.iot.pcs.service.PluginService;
import lk.dialog.iot.pcs.service.impl.PluginServiceImpl;
import lk.dialog.iot.pcs.util.Constants;
import lk.dialog.iot.pcs.util.EnumTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lk.dialog.iot.pcs.exception.impl.PluginBehaviorException;
import lk.dialog.iot.pcs.service.PluginBehavior;
import ro.fortsoft.pf4j.DefaultPluginManager;
import ro.fortsoft.pf4j.ExtensionFactory;
import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.spring.SpringExtensionFactory;

@SpringBootApplication
@IntegrationComponentScan
@EnableTransactionManagement
//@EnableCaching
@EnableScheduling
@EnableAsync

@ComponentScan({"lk.dialog.iot.pcs"})
public class Application implements CommandLineRunner{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    private PluginService pluginService;

    @Value("${dir.plugins}")
    private String dirPlugins;

    public static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

    @Bean
    public PluginManager pluginManager() {
        File file = new File(dirPlugins);
        PluginManager pluginManager = new DefaultPluginManager(file) {

            @Override
            protected ExtensionFactory createExtensionFactory() {
                return new SpringExtensionFactory(this);
            }

        };
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        return pluginManager;
    }

    @Bean
    public Map<String, PluginBehavior> loadPluginMap(PluginManager pluginManager)
            throws PluginBehaviorException {

        Map<String, PluginBehavior> pluginMap = new HashMap<String, PluginBehavior>();
        List<PluginBehavior> pluginList = pluginManager.getExtensions(PluginBehavior.class);

        if (pluginList != null && !pluginList.isEmpty()) {
            Iterator<PluginBehavior> itrPluginService = pluginList.iterator();
            while (itrPluginService.hasNext()) {
                PluginBehavior pluginBehavior = itrPluginService.next();
                pluginMap.put(pluginBehavior.toString(), pluginBehavior);
            }
        }
        logger.info("Pf4j resolved {} plugins", pluginMap.size());
        logger.info("Plugins map : {}.", pluginMap);
        return pluginMap;
    }
 
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void run(String... strings) throws Exception {
        logger.info("CommandLineRunner Started.");

//        consumerservice.callExternalService("","");
//        HashMap<String, Object> x = new HashMap<>();
//        pluginService.executePluginOperation(x);
    }
}
