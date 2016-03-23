package com.baidu.unbiz.report.engine.log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 线程执行的上下文内容
 * 
 * @author wangchongjie
 * @fileName ThreadContext.java
 * @dateTime 2015-7-15 下午3:21:06
 */
public class ThreadLogContext {

    /**
     * 线程上下文变量的持有者
     */
    private final static ThreadLocal<Map<String, Object>> CTX_HOLDER = new ThreadLocal<Map<String, Object>>();

    static {
        CTX_HOLDER.set(new HashMap<String, Object>());
    }

    /**
     * 用来做分库分表的切分ID
     */
    private final static String OLAP_SHARD_KEY = "olapShardKey";

    /**
     * 线程的日志级别
     */
    private final static String THREAD_LOG_KEY = "threadLog";

    /**
     * 添加内容到线程上下文中
     * 
     * @param key
     * @param value
     */
    public final static void putContext(String key, Object value) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx == null) {
            init();
            ctx = CTX_HOLDER.get();
        }
        ctx.put(key, value);
    }

    /**
     * 从线程上下文中获取内容
     * 
     * @param key
     */
    @SuppressWarnings("unchecked")
    public final static <T extends Object> T getContext(String key) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx == null) {
            return null;
        }
        return (T) ctx.get(key);
    }

    /**
     * 获取线程上下文
     * 
     * @param key
     */
    public final static Map<String, Object> getContext() {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx == null) {
            return null;
        }
        return ctx;
    }

    /**
     * 删除上下文中的key
     * 
     * @param key
     */
    public final static void remove(String key) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx != null) {
            ctx.remove(key);
        }
    }

    /**
     * 上下文中是否包含此key
     * 
     * @param key
     * @return
     */
    public final static boolean contains(String key) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx != null) {
            return ctx.containsKey(key);
        }
        return false;
    }

    /**
     * 清空线程上下文
     */
    public final static void clean() {
        CTX_HOLDER.set(null);
    }

    /**
     * 初始化线程上下文
     */
    public final static void init() {
        CTX_HOLDER.set(new HashMap<String, Object>());
    }

    /**
     * 获取用来做分库分表的key
     * 
     * @return OlapShardKey
     * @since 2015-7-15 by wangchongjie
     */
    @SuppressWarnings("unchecked")
    public static final <K extends Serializable> K getOlapShardKey() {
        return (K) getContext(OLAP_SHARD_KEY);
    }

    /**
     * 设置做分表分库的切分的key
     * 
     * @param shardKey
     * @since 2015-7-15 by wangchongjie
     */
    public static final <K extends Serializable> void putOlapShardKey(K shardKey) {
        putContext(OLAP_SHARD_KEY, shardKey);
    }

    /**
     * 线程日志的级别
     * 
     * @param logLevel
     * @since 2015-7-15 by wangchongjie
     */
    public static final void putThreadLog(Integer logLevel) {
        putContext(THREAD_LOG_KEY, logLevel);
    }

    /**
     * 获取线程日志的级别
     * 
     * @return
     * @since 2015-7-15 by wangchongjie
     */
    public static final Integer getThreadLog() {
        return getContext(THREAD_LOG_KEY);
    }
}
