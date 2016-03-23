package com.baidu.unbiz.report.engine.exception;

/**
 * 缓存类异常
 */
public class CacheException extends RuntimeException {

    private static final long serialVersionUID = -193202262468464650L;

    public CacheException() {
        super();
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }

}
