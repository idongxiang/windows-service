package io.dongxiang.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Email: i@dongxiang.io
 * Github: https://github.com/idongxiang
 * Blog: https://blog.dongxiang.io
 *
 * @author dongxiang
 * @since 2019/4/18
 */
@RestController
public class EchoController {
    private static final Logger logger = LoggerFactory.getLogger(EchoController.class);

    @RequestMapping(value = {"/", "/index", "index.html"}, method = {RequestMethod.POST, RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String echo(@RequestBody(required = false) String requestBody, HttpServletRequest request) {
        try {
            StringBuilder headers = new StringBuilder();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.append(headerName).append(":").append(request.getHeader(headerName)).append(";");
            }
            if (headers.length() > 0) {
                headers.deleteCharAt(headers.length() - 1);
            }
            String queryString = StringUtils.isBlank(request.getQueryString()) ? "" : "?" + request.getQueryString();
            String url = request.getRequestURI() + queryString;
            String remoteHost = request.getRemoteHost();
            logger.info("RemoteHost={},Url={},RequestBody={},Headers={},", remoteHost, url, requestBody, headers);
        } catch (Throwable e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return "OK";
    }

    @RequestMapping(value = "/echo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String echo(@RequestHeader(name = "User-Agent", required = false) String ua, @RequestHeader HttpHeaders headers, @RequestParam(required = false) Map<String, Object> params, @RequestBody(required = false) String body) {
        logger.info("User-Agent:" + StringUtils.defaultString(ua, "N/A"));

        Map<String, Object> payload = new HashMap<>(10);

        payload.put("Echo", UserAgent.parseUserAgentString(ua));
        payload.put("Parameters", params.size() == 0 ? "N/A" : params);
        payload.put("Headers", headers.size() == 0 ? "N/A" : headers);

        try {
            payload.put("Payload", JSON.parseObject(StringUtils.defaultString(body, "N/A")));
        } catch (Exception e) {
            payload.put("Payload", StringUtils.defaultString(body, "N/A"));
        }

        logger.info("Payload   :\n" + JSON.toJSONString(payload, SerializerFeature.PrettyFormat));

        return JSON.toJSONString(payload, SerializerFeature.PrettyFormat);
    }

}
