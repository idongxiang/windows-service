package io.dongxiang.controller;

import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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

    @RequestMapping({"/exec"})
    @ResponseBody
    public String exec(@RequestHeader("User-Agent") String ua, @RequestParam(name = "command") String command) {
        logger.info(String.format("Welcome %s !", UserAgent.parseUserAgentString(ua)));
        String exec = exec(command);
        logger.info(String.format("Exec Print:\n%s", exec));
        Parser parser = Parser.builder().build();
        String input = String.format("```\n%s\n```", exec);
        Node document = parser.parse(input);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return "<h1>Run Command OK!</h1>" + renderer.render(document);
    }

    private String exec(String command) {
        try {
            //关机
            Process exec = Runtime.getRuntime().exec(command);
            InputStream is = exec.getInputStream();
            return IOUtils.toString(is, Charset.forName("GBK"));
        } catch (Throwable e) {
            return ExceptionUtils.getStackTrace(e);
        }
    }

    private void shutdown(Long seconds) {
        seconds = Objects.isNull(seconds) || seconds < 300L ? 300L : seconds;
        String command = "shutdown /s /t " + seconds;
        try {
            //关机
            Runtime.getRuntime().exec(command);
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
