package io.dongxiang.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * Email: i@dongxiang.io
 * Github: https://github.com/idongxiang
 * Blog: https://blog.dongxiang.io
 *
 * @author dongxiang
 * @since 2019/4/18
 */
@Service
public class DaemonService {
    private static final Logger logger = LoggerFactory.getLogger(DaemonService.class);

    @Value("${daemon.request.url}")
    private String url;

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        new Thread(this::forever).start();
    }

    private final static long ONE_MINUTES = 60 * 1000;

    private void forever() {

        while (true) {
            try {
                ResponseEntity<String> response = this.restTemplate.getForEntity(url, String.class);
                logger.info("ResponseBody={}", response.getBody());
                try {
                    Thread.sleep(ONE_MINUTES);
                } catch (InterruptedException ignore) {
                }
            } catch (RestClientException ignore) {
            }
        }

    }

}
