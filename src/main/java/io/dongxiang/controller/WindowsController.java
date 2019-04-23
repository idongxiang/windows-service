package io.dongxiang.controller;

import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private AtomicBoolean taskExist = new AtomicBoolean();
    private final ConcurrentHashMap<String, String> locks = new ConcurrentHashMap<>();

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
        return "<h1>Run Command OK!</h1>" + render(exec);
    }

    @RequestMapping({"/tasklist"})
    @ResponseBody
    public String taskList(@RequestHeader("User-Agent") String ua) {
        logger.info(String.format("Welcome %s !", UserAgent.parseUserAgentString(ua)));
        String exec = exec("TASKLIST");
        logger.info(String.format("Exec Print:\n%s", exec));
        return "<h1>Run Command OK!</h1>" + render(exec);
    }

    @RequestMapping({"/taskkill"})
    @ResponseBody
    public String exec(@RequestHeader("User-Agent") String ua,
                       @RequestParam(name = "seconds", required = false) Long seconds,
                       @RequestParam(name = "image_name") String imageName) {
        logger.info(String.format("Welcome %s !", UserAgent.parseUserAgentString(ua)));
        if (StringUtils.isBlank(imageName)) {
            return "<h1>Image Name Can Not Empty !</h1>";
        }

        String command = "TASKLIST /FI \"IMAGENAME eq " + imageName + "\"";
        String taskList = exec(command);
        if (StringUtils.isBlank(taskList) || !taskList.contains(imageName)) {
            return "<h1>Not Found Process : " + imageName + "!</h1>" + render(taskList);
        }

        if (locks.containsKey(imageName)
                || null != locks.putIfAbsent(imageName, imageName)) {
            return "<h1>Already Exist Task Kill Job For Image Name [" + imageName + "]!</h1>";
        }

        final long sleepMillis = Objects.isNull(seconds) || seconds < 180 ? 180 * 1000 : seconds * 1000;

        new Thread(() -> {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
            String c = "TASKKILL /F /IM " + imageName + " /T";
            String exec = exec(c);
            locks.remove(imageName);
            logger.info(String.format("Unlock For [" + imageName + "], Exec Print:\n%s", exec));
        }).start();

        return "<h1>Task Kill [" + imageName + "] Will In " + sleepMillis / 1000 + " Seconds After!</h1>" + render(taskList);
    }

    private String render(String text) {
        Parser parser = Parser.builder().build();
        String input = String.format("```\n%s\n```", text);
        Node document = parser.parse(input);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
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
