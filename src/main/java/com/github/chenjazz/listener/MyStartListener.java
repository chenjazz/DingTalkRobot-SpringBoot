package com.github.chenjazz.listener;

import com.alibaba.fastjson.JSON;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.github.chenjazz.listener.MapFactory.of;

/**
 * @author 陈佳志
 * 2017-10-28
 */

public class MyStartListener implements ApplicationListener<ApplicationStartingEvent> {

    public static long start = System.currentTimeMillis();


    private static final Logger log = LoggerFactory.getLogger(MyStartListener.class);

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {

        SimpleCommandLinePropertySource commandLinePropertySource = new SimpleCommandLinePropertySource(event.getArgs());

        if (commandLinePropertySource.containsProperty("ding-url")) {

            final String name = ListenerUtils.getServerName(commandLinePropertySource.getProperty("ding-app-name"));

            //ding-url  ding-msg
            Map<String, Object> map = of(
                    "msgtype", "text",
                    "text", of("content", "【[出差]】" + name + "正在启动。\n 当前时间：" +
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")))
            );

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String url = commandLinePropertySource.getProperty("ding-url");
                    try {
                        String returnString = Request.Post(url).connectTimeout(8000)
                                .bodyString(JSON.toJSONString(map), ContentType.APPLICATION_JSON).execute().returnContent().asString();
                        if (log.isInfoEnabled()) {
                            log.info("钉钉通知已发送 return:" + returnString);
                        } else {
                            System.out.println("钉钉通知已发送 return: + returnString");
                        }
                    } catch (Exception e) {
                        log.warn("发送钉钉通知失败", e);
                    }
                }
            }.start();
        } else {
            if (log.isInfoEnabled()) {
                log.info("程序开始启动：钉钉群通知命令行参数 [--ding-url]，[--ding-app-name]未配置");
            } else {
                System.out.println("程序开始启动：钉钉群通知命令行参数 [--ding-url]，[--ding-app-name]未配置");
            }
        }
    }
}
