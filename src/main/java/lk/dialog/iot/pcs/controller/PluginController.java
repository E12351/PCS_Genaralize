package lk.dialog.iot.pcs.controller;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lk.dialog.iot.pcs.dto.PluginDto;
import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;
import lk.dialog.iot.pcs.model.Plugin;
import lk.dialog.iot.pcs.service.PluginService;

@RestController
public class PluginController extends BaseController {

    @Autowired
    private PluginService pluginService;

    /**
     * @api {get} http://HostName:PORT/api/plugins Get plugins
     * @apiName GetPlugins
     * @apiGroup ProtocolConverterServicePlugin
     *
     * @apiSuccess {Plugin} plugins List of Plugins
     * @apiSuccess {Integer} plugins.id The unique identifier of the plugin
     * @apiSuccess {String} plugins.endPoint The path variable value
     * @apiSuccess {String} plugins.eventMessageRegex The event matching pattern
     * @apiSuccess {String} plugins.eventPublishTopic The event publishing topic
     * @apiSuccess {String} plugins.actionPublishTopic The action responses
     *             publishing topic
     * @apiSuccess {String} plugins.actionSubscribeTopicRegex The action subscribing
     *             topic pattern
     * @apiSuccess {String} plugins.actionUrl The URL for call action
     * @apiSuccess {String} plugins.plugunName The plugin name
     * @apiSuccess {String} plugins.parameters The additional parameters of plugin
     * @apiSuccess {Integer} plugins.deviceDefinitionId The device definition id of
     *             the plugin
     *
     * @apiSuccessExample {json} Success-Response:
     *                    [{"id":1,"endPoint":"smsDevice","eventMessageRegex":"Security
     *                    system to remind
     *                    you:","eventPublishTopic":"smarthomegsmm1/pcs/common","actionPublishTopic":"/smarthomegsmm1/pcs/pub","actionSubscribeTopicRegex":".smarthomegsmm1.pcs.sub","actionUrl":"http://192.168.8.106:27774/api/actionPluginService","plugunName":"Smart_Home_Gsm_M1_Pcs_Plugin","parameters":"user:user1,password:****","deviceDefinitionId":10}]
     */
    @RequestMapping(value = "/api/plugins", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<PluginDto>> getPlugins(HttpServletRequest request)
            throws ProtocolConverterException {
        System.out.println("passed.");
//        setLogIdentifier(request);
//        checkAuthentication(request, INTERNAL_ADMIN);
        Collection<Plugin> plugins = pluginService.findAll();
        Collection<PluginDto> pluginDtos = new ArrayList<PluginDto>();

        System.out.println(pluginDtos);

        for (Plugin plugin : plugins) {
            pluginDtos.add(pluginService.fromPluginToPluginDto(plugin));
        }
        return new ResponseEntity<Collection<PluginDto>>(pluginDtos, HttpStatus.OK);
    }

    /**
     * @api {get} http://HostName:PORT/api/plugins/:id Get plugin by id
     * @apiName GetPluginById
     * @apiGroup ProtocolConverterServicePlugin
     *
     * @apiParam {Number} id The unique id of the plugin
     *
     * @apiSuccess {Integer} plugins.id The unique identifier of the plugin
     * @apiSuccess {String} plugins.endPoint The path variable value
     * @apiSuccess {String} plugins.eventMessageRegex The event matching pattern
     * @apiSuccess {String} plugins.eventPublishTopic The event publishing topic
     * @apiSuccess {String} plugins.actionPublishTopic The action responses
     *             publishing topic
     * @apiSuccess {String} plugins.actionSubscribeTopicRegex The action subscribing
     *             topic pattern
     * @apiSuccess {String} plugins.actionUrl The URL for call action
     * @apiSuccess {String} plugins.plugunName The plugin name
     * @apiSuccess {String} plugins.parameters The additional parameters of plugin
     * @apiSuccess {Integer} plugins.deviceDefinitionId The device definition id of
     *             the plugin
     *
     * @apiSuccessExample {json} Success-Response:
     *                    {"id":1,"endPoint":"smsDevice","eventMessageRegex":"Security
     *                    system to remind
     *                    you:","eventPublishTopic":"smarthomegsmm1/pcs/common","actionPublishTopic":"/smarthomegsmm1/pcs/pub","actionSubscribeTopicRegex":".smarthomegsmm1.pcs.sub","actionUrl":"http://192.168.8.106:27774/api/actionPluginService","plugunName":"Smart_Home_Gsm_M1_Pcs_Plugin","parameters":"user:user1,password:****","deviceDefinitionId":10}
     */
    @RequestMapping(value = "/api/plugins/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PluginDto> getPlugin(@PathVariable("id") int id, HttpServletRequest request)
            throws ProtocolConverterException {
        setLogIdentifier(request);
        checkAuthentication(request, INTERNAL_ADMIN);
        Plugin plugin = pluginService.findOne(id);
        if (plugin == null) {
            return new ResponseEntity<PluginDto>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<PluginDto>(pluginService.fromPluginToPluginDto(plugin), HttpStatus.OK);
    }

    /**
     * @api {post} http://HostName:PORT/api/plugins Add new plugin
     * @apiName AddPlugin
     * @apiGroup ProtocolConverterServicePlugin
     *
     * @apiParam {String} plugins.endPoint The path variable value
     * @apiParam {String} plugins.eventMessageRegex The event matching pattern
     * @apiParam {String} plugins.eventPublishTopic The event publishing topic
     * @apiParam {String} plugins.actionPublishTopic The action responses publishing
     *           topic
     * @apiParam {String} plugins.actionSubscribeTopicRegex The action subscribing
     *           topic pattern
     * @apiParam {String} plugins.actionUrl The URL for call action
     * @apiParam {String} plugins.plugunName The plugin name
     * @apiParam {String} plugins.parameters The additional parameters of plugin
     * @apiParam {Integer} plugins.deviceDefinitionId The device definition id of
     *           the plugin
     *
     * @apiSuccess {Integer} plugins.id The unique identifier of the plugin
     * @apiSuccess {String} plugins.endPoint The path variable value
     * @apiSuccess {String} plugins.eventMessageRegex The event matching pattern
     * @apiSuccess {String} plugins.eventPublishTopic The event publishing topic
     * @apiSuccess {String} plugins.actionPublishTopic The action responses
     *             publishing topic
     * @apiSuccess {String} plugins.actionSubscribeTopicRegex The action subscribing
     *             topic pattern
     * @apiSuccess {String} plugins.actionUrl The URL for call action
     * @apiSuccess {String} plugins.plugunName The plugin name
     * @apiSuccess {String} plugins.parameters The additional parameters of plugin
     * @apiSuccess {Integer} plugins.deviceDefinitionId The device definition id of
     *             the plugin
     *
     * @apiSuccessExample {json} Success-Response:
     *                    {"id":1,"endPoint":"smsDevice","eventMessageRegex":"Security
     *                    system to remind
     *                    you:","eventPublishTopic":"smarthomegsmm1/pcs/common","actionPublishTopic":"/smarthomegsmm1/pcs/pub","actionSubscribeTopicRegex":".smarthomegsmm1.pcs.sub","actionUrl":"http://192.168.8.106:27774/api/actionPluginService","plugunName":"Smart_Home_Gsm_M1_Pcs_Plugin","parameters":"user:user1,password:****","deviceDefinitionId":10}
     */
    @RequestMapping(value = "/api/plugins", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PluginDto> createPlugin(@Valid @RequestBody PluginDto pluginDto, HttpServletRequest request)
            throws ProtocolConverterException {
        setLogIdentifier(request);
//        checkAuthentication(request, INTERNAL_ADMIN);
        Plugin savedPlugin = pluginService.create(pluginService.fromPluginDtoToPlugin(pluginDto));
        return new ResponseEntity<PluginDto>(pluginService.fromPluginToPluginDto(savedPlugin), HttpStatus.CREATED);
    }

    /**
     * @api {put} http://HostName:PORT/api/plugins/:id Update plugin
     * @apiName UpdatePlugin
     * @apiGroup ProtocolConverterServicePlugin
     *
     * @apiParam {Number} id The unique id of the plugin
     * @apiParam {String} plugins.endPoint The path variable value
     * @apiParam {String} plugins.eventMessageRegex The event matching pattern
     * @apiParam {String} plugins.eventPublishTopic The event publishing topic
     * @apiParam {String} plugins.actionPublishTopic The action responses publishing
     *           topic
     * @apiParam {String} plugins.actionSubscribeTopicRegex The action subscribing
     *           topic pattern
     * @apiParam {String} plugins.actionUrl The URL for call action
     * @apiParam {String} plugins.plugunName The plugin name
     * @apiParam {String} plugins.parameters The additional parameters of plugin
     * @apiParam {Integer} plugins.deviceDefinitionId The device definition id of
     *           the plugin
     *
     * @apiSuccess {Integer} plugins.id The unique identifier of the plugin
     * @apiSuccess {String} plugins.endPoint The path variable value
     * @apiSuccess {String} plugins.eventMessageRegex The event matching pattern
     * @apiSuccess {String} plugins.eventPublishTopic The event publishing topic
     * @apiSuccess {String} plugins.actionPublishTopic The action responses
     *             publishing topic
     * @apiSuccess {String} plugins.actionSubscribeTopicRegex The action subscribing
     *             topic pattern
     * @apiSuccess {String} plugins.actionUrl The URL for call action
     * @apiSuccess {String} plugins.plugunName The plugin name
     * @apiSuccess {String} plugins.parameters The additional parameters of plugin
     * @apiSuccess {Integer} plugins.deviceDefinitionId The device definition id of
     *             the plugin
     *
     * @apiSuccessExample {json} Success-Response:
     *                    {"id":1,"endPoint":"smsDevice","eventMessageRegex":"Security
     *                    system to remind
     *                    you:","eventPublishTopic":"smarthomegsmm1/pcs/common","actionPublishTopic":"/smarthomegsmm1/pcs/pub","actionSubscribeTopicRegex":".smarthomegsmm1.pcs.sub","actionUrl":"http://192.168.8.106:27774/api/actionPluginService","plugunName":"Smart_Home_Gsm_M1_Pcs_Plugin","parameters":"user:user1,password:****","deviceDefinitionId":10}
     */
    @RequestMapping(value = "/api/plugins/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PluginDto> updatePlugin(@PathVariable("id") Integer id,
            @Valid @RequestBody PluginDto pluginDto, HttpServletRequest request) throws ProtocolConverterException {
        setLogIdentifier(request);
        checkAuthentication(request, INTERNAL_ADMIN);
        Plugin updatedPlugin = pluginService.update(id, pluginService.fromPluginDtoToPlugin(pluginDto));
        if (updatedPlugin == null) {
            return new ResponseEntity<PluginDto>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<PluginDto>(pluginService.fromPluginToPluginDto(updatedPlugin), HttpStatus.OK);
    }

    /**
     * @api {delete} http://HostName:PORT/api/plugins/:id Delete plugin.
     * @apiName DeletePlugin
     * @apiGroup ProtocolConverterServicePlugin
     *
     * @apiParam {Number} id The unique id of the plugin
     */
    @RequestMapping(value = "/api/plugins/{id}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PluginDto> deletePlugin(@PathVariable("id") Integer id, HttpServletRequest request)
            throws ProtocolConverterException {
        setLogIdentifier(request);
        checkAuthentication(request, INTERNAL_ADMIN);
        pluginService.delete(id);
        return new ResponseEntity<PluginDto>(HttpStatus.NO_CONTENT);
    }

    /**
     * @api {post} http://HostName:PORT/api/pluginUpload Upload and deploy new
     *      plugin.
     * @apiName ProtocolConverterServicePlugin
     * @apiGroup Plugins
     *
     * @apiParam {MultipartFile} file The file to upload
     *
     */
    @RequestMapping(value = "/api/pluginUpload", method = RequestMethod.POST)
    public ResponseEntity<Boolean> savePluginFile(
            @RequestParam(value = "pluginFile", required = true) MultipartFile pluginFile, HttpServletRequest request)
            throws ProtocolConverterException {
//        setLogIdentifier(request);
//        checkAuthentication(request, INTERNAL_ADMIN);
        boolean status = pluginService.uploadPluginFile(pluginFile);
        System.out.println("Uploaded.");
        if (status) {
            return new ResponseEntity<Boolean>(HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
