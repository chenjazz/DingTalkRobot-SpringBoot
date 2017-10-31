package com.github.chenjazz.listener;

import com.alibaba.fastjson.JSON;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.util.Map;

import static com.github.chenjazz.listener.MapFactory.of;

/**
 * @author 陈佳志
 * 2017-10-28
 */

public class MyFailListener implements ApplicationListener<ApplicationFailedEvent> {


    private static final Logger log = LoggerFactory.getLogger(MyFailListener.class);

    @SuppressWarnings("ALL")
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {

        SimpleCommandLinePropertySource commandLinePropertySource = new SimpleCommandLinePropertySource(event.getArgs());


        if (commandLinePropertySource.containsProperty("ding-url")) {
            final String name = ListenerUtils.getServerName(commandLinePropertySource.getProperty("ding-app-name"));

            //ding-url  ding-msg
            Map<String, Object> map = of(
                    "msgtype", "text",
                    "text", of("content", "【[对不起]】" + name + "启动失败。")
            );

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String url = commandLinePropertySource.getProperty("ding-url");
                    try {
                        String returnString = Request.Post(url).connectTimeout(8000)
                                .bodyString(JSON.toJSONString(map), ContentType.APPLICATION_JSON).execute().returnContent().asString();
                        log.info("钉钉通知已发送 return:" + returnString);
                    } catch (Exception e) {
                        log.warn("发送钉钉通知失败", e);
                    }
                }
            }.start();
        }
    }
}
