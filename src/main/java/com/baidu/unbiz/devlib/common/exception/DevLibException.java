package com.baidu.unbiz.devlib.common.exception;

public class DevLibException extends RuntimeException {

    private static final long serialVersionUID = 1935872558909331529L;

    public DevLibException(String msg) {
        super(msg);
    }

    public DevLibException(Throwable cause) {
        super(cause);
    }

    public DevLibException(String message, Throwable cause) {
        super(message, cause);
    }
}
