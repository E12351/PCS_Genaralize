package lk.dialog.iot.pcs.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;
import lk.dialog.iot.pcs.service.HttpEndpointService;
import lk.dialog.iot.pcs.util.Constants;

@RestController
public class HttpEndpointController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HttpEndpointService httpEndpointService;

    /**
     * @api {post} http://HostName:PORT/api/pcs/:endpoint Http event/action request end-point
     * @apiName Event/Action Endpoint
     * @apiGroup ProtocolConverterServicePlugin
     *
     * @apiParam {String} endpoint The path variable value of endpoint
     *
     *@apiSuccess {String} status The status of request
     *
     * @apiSuccessExample {json} Success-Response:
            {"status": "Success"}
     */
    @RequestMapping(value = "/api/pis/{endpoint}", method = RequestMethod.POST)
    public ResponseEntity<String> captureEvent(@PathVariable("endpoint") String endpoint,
            @RequestBody(required = false) Object body, HttpServletRequest request) throws ProtocolConverterException {
        setLogIdentifier(request);
        checkAuthentication(request, INTERNAL_ADMIN);

        logger.info("Post request received at the endpoint : {}, message : {}.", endpoint, body);

        if (body == null || endpoint == null) {
            logger.warn("The body or endpoint is empty.");
            return new ResponseEntity<String>(Constants.FAIL_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        httpEndpointService.publishToBroker(endpoint, body);
        return new ResponseEntity<String>(Constants.SUCCESS_MESSAGE, HttpStatus.OK);
    }
}