package lk.dialog.iot.pcs.controller;

import java.util.Map;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lk.dialog.iot.pcs.exception.ExceptionAttributes;
import lk.dialog.iot.pcs.exception.impl.DefaultExceptionAttributes;
import lk.dialog.iot.pcs.exception.impl.ProtocolConverterException;
import lk.dialog.iot.pcs.util.Constants;

public class BaseController {

    // TODO : Consider common project for all services
    protected static final int ADMIN = 10;
    protected static final int INTERNAL_ADMIN = 5;
    protected static final int USER = 1;

    private static final String appHeaderName = "X-JWT-Assertion";
    private static final String authHeaderName = "iot-auth";

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected ModelMapper modelMapper;

    @Value("${url.authenticationService}")
    private String authenticationServiceUrl;

    @Value("${api.security.enabled}")
    private boolean apiSecurityEnabled;

    @ExceptionHandler(ProtocolConverterException.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception exception, HttpServletRequest request) {

        ExceptionAttributes exceptionAttributes = new DefaultExceptionAttributes();
        Map<String, Object> responseBody = exceptionAttributes.getExceptionAttributes(exception, request,
                HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<Map<String, Object>> handleNoResultException(NoResultException noResultException,
            HttpServletRequest request) {

        ExceptionAttributes exceptionAttributes = new DefaultExceptionAttributes();

        Map<String, Object> responseBody = exceptionAttributes.getExceptionAttributes(noResultException, request,
                HttpStatus.NOT_FOUND);

        return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.NOT_FOUND);
    }

    public void checkAuthentication(HttpServletRequest request, int expectedUserRole)
            throws ProtocolConverterException {

        String appInfoHeader = request.getHeader(appHeaderName);
        String authInfoHeader = request.getHeader(authHeaderName);

        if (apiSecurityEnabled) {
            try {
                String userRoleStr = Integer.toString(expectedUserRole);

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("appInfo", appInfoHeader);
                headers.add("authInfo", authInfoHeader);
                headers.add("expectedRole", userRoleStr);
                headers.add(Constants.LOG_IDENTIFIER_KEY, MDC.get(Constants.LOG_IDENTIFIER_KEY));

                HttpEntity<Object> httpEntity = new HttpEntity<Object>(headers);
                boolean accessResponse = restTemplate.postForObject(authenticationServiceUrl, httpEntity,
                        Boolean.class);

                if (accessResponse == false) {
                    throw new ProtocolConverterException("Access Denied.");
                }
            } catch (HttpClientErrorException exp) {
                logger.error("http post call exception : " + exp);
            }
        }
    }

    public void setLogIdentifier(HttpServletRequest request) {
        String logIdentifier = request.getHeader(Constants.LOG_IDENTIFIER_KEY);

        if (logIdentifier != null) {
            MDC.put(Constants.LOG_IDENTIFIER_KEY, logIdentifier);
        } else {
            MDC.put(Constants.LOG_IDENTIFIER_KEY, UUID.randomUUID().toString());
        }
    }

}
