package io.dongxiang.controller;

import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

/**
 * Email: idongxiang@qq.com
 * Github: https://github.com/idongxiang
 *
 * @author dongxiang
 * @since 2019/2/21
 */
@RestController
public class WindowsController {
    private static final Logger logger = LoggerFactory.getLogger(WindowsController.class);

    @RequestMapping({"/shutdown"})
    @ResponseBody
    public String shutdown(@RequestHeader("User-Agent") String ua, @RequestParam(name = "seconds", required = false) Long seconds) {
        logger.info(String.format("Welcome %s !", UserAgent.parseUserAgentString(ua)));
        shutdown(seconds);
        return "<h1>Shutdown Command OK!</h1>";
    }

    @RequestMapping({"/rollback"})
    @ResponseBody
    public String rollback(@RequestHeader("User-Agent") String ua) {
        logger.info(String.format("Welcome %s !", UserAgent.parseUserAgentString(ua)));
        rollback();
        return "<h1>Rollback Command OK!</h1>";
    }

    private void shutdown(Long seconds) {
        seconds = Objects.isNull(seconds) || seconds < 300L ? 300L : seconds;
        try {
            //关机
            Runtime.getRuntime().exec("shutdown /s /t " + seconds);
        } catch (IOException ignore) {
        }
    }

    private void rollback() {
        try {
            //取消关机
            Runtime.getRuntime().exec("shutdown /a");
        } catch (IOException ignore) {
        }
    }
}
