package com.baidu.unbiz.devlib.time;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 线程处理耗时记录器
 *
 * @author wangchongjie
 * @fileName TimeRecoder.java
 * @dateTime 2015-5-10 下午9:44:31
 */
public class TimeRecoder {
    private static final Log LOG = LogFactory.getLog(TimeRecoder.class);

    private long time;
    private String token = "";

    private TimeRecoder() {
    }

    public static TimeRecoder newTimeRecoder() {
        return newTimeRecoder("");
    }

    public static TimeRecoder newTimeRecoder(TimeAware ta) {
        return newTimeRecoder(ta.timeRecoderFlag());
    }

    public static TimeRecoder newTimeRecoder(String flag) {
        TimeRecoder out = new TimeRecoder();
        out.setTime(System.currentTimeMillis());
        String uuid = java.util.UUID.randomUUID().toString();
        out.setToken(uuid.substring(0, Math.min(4, uuid.length())) + "-" + flag);
        return out;
    }

    public void record(String msg) {
        long now = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(token);
        sb.append("-").append(msg).append(":").append(now - time);
        LOG.info(sb);
        time = now;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
