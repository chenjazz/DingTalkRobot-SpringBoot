package com.github.chenjazz.listener;

import com.alibaba.fastjson.JSON;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.util.Map;

import static com.github.chenjazz.listener.MapFactory.of;


/**
 * @author 陈佳志
 * 2017-10-28
 */

public class MySuccessListener implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(MySuccessListener.class);

    @Override
    @SuppressWarnings("Duplicates")
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SimpleCommandLinePropertySource commandLinePropertySource = new SimpleCommandLinePropertySource(event.getArgs());

        if (commandLinePropertySource.containsProperty("ding-url")) {
            float secUse = (System.currentTimeMillis() - MyStartListener.start) / 1000F;
            final String name = ListenerUtils.getServerName(commandLinePropertySource.getProperty("ding-app-name"));
            final String url = commandLinePropertySource.getProperty("ding-url");

            //ding-url  ding-msg
            Map<String, Object> map = of(
                    "msgtype", "text",
                    "text", of("content", "【[OK]】" + name + "启动完成，用时" + secUse + "秒。")
            );


            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String returnString;
                    try {
                        returnString = Request.Post(url).connectTimeout(8000)
                                .bodyString(JSON.toJSONString(map), ContentType.APPLICATION_JSON).execute().returnContent().asString();
                        log.info("钉钉通知已发送 return:" + returnString);
                    } catch (Exception e) {
                        log.warn("发送钉钉通知失败", e);
                    }
                }
            }.start();


            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    String timeStatistics = ListenerUtils.getTimeStatistics((System.currentTimeMillis() - MyStartListener.start) / 1000);
                    super.run();
                    Map<String, Object> mapStop = of(
                            "msgtype", "text",
                            "text", of("content", "【[再见]】" + name + "正在停止。自从上次启动以来，已稳定运行" + timeStatistics + "。")
                    );

                    try {
                        String returnString = Request.Post(url).connectTimeout(8000)
                                .bodyString(JSON.toJSONString(mapStop), ContentType.APPLICATION_JSON).execute().returnContent().asString();
                        log.info("钉钉通知已发送 return:" + returnString);
                    } catch (Exception e) {
                        log.warn("发送钉钉通知失败", e);
                    }
                }
            });
        }
    }
}
