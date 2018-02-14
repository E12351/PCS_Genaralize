package lk.dialog.iot.pcs.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;
import lk.dialog.iot.pcs.service.ScheduleService;
import lk.dialog.iot.pcs.util.Constants;

@RestController
public class ScheduleController extends BaseController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * @api {post} http://HostName:PORT/api/schedule/:deviceId Schedule request as event
     * @apiName ScheduleToEvent
     * @apiGroup ProtocolConverterServicePlugin
     *
     * @apiParam {Number} deviceId The path variable value of device id
     *
     * @apiSuccess {String} status The status of request
     *
     * @apiSuccessExample {json} Success-Response-Status:
           {"status" : "success"}
     */
    @RequestMapping(value = "/api/schedule/{deviceId}", method = RequestMethod.POST)
    public ResponseEntity<String> captureEvent(@PathVariable("deviceId") int deviceId, HttpServletRequest request)
            throws ProtocolConverterException {
        setLogIdentifier(request);
        checkAuthentication(request, INTERNAL_ADMIN);

        logger.info("Post request received at the schedule device id : {}.", deviceId);

        if (deviceId > 0) {
            scheduleService.executeSchedule(deviceId);
            return new ResponseEntity<String>(Constants.SUCCESS_MESSAGE, HttpStatus.OK);
        } else {
            logger.error("Invalid device id as : {}.", deviceId);
            return new ResponseEntity<String>(Constants.FAIL_MESSAGE, HttpStatus.BAD_REQUEST);
        }
    }
}
