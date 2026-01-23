package com.finalshell.zmodem;

/**
 * Zmodem传输异常
 */
public class ZmodemException extends Exception {
    public ZmodemException(String message) {
        super(message);
    }
    
    public ZmodemException(String message, Throwable cause) {
        super(message, cause);
    }
}
