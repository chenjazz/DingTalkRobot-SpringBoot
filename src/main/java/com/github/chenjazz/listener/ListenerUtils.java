package com.github.chenjazz.listener;

import org.springframework.util.StringUtils;

/**
 * @author 陈佳志
 * 2017-10-28
 */
public class ListenerUtils {

    static String getServerName(String argName) {
        if (StringUtils.isEmpty(argName)) {
            return "服务器";
        } else {
            return argName + " ";
        }
    }

    static String getTimeStatistics(long seconds) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean beforeAlreadyShow = false;
        long d = seconds / 86400;
        if (d > 0) {
            stringBuilder.append(d).append("天");
            beforeAlreadyShow = true;
        }

        long h = (seconds - d * 86400) / 3600;
        if (beforeAlreadyShow) {
            stringBuilder.append(h).append("小时");
        } else {
            if (h > 0) {
                stringBuilder.append(h).append("小时");
                beforeAlreadyShow = true;
            }
        }

        long m = (seconds - d * 86400 - h * 3600) / 60;
        if (beforeAlreadyShow) {
            stringBuilder.append(m).append("分钟");
        } else {
            if (m > 0) {
                stringBuilder.append(m).append("分钟");
                beforeAlreadyShow = true;
            }
        }
        long s = (seconds - d * 86400 - h * 3600 - m * 60);
        if (s > 0) {
            stringBuilder.append(s).append("秒");
        }
        return stringBuilder.toString();
    }
}
