package com.finalshell.vnc;

/**
 * VNC Exception
 */
public class VNCException extends Exception {
    
    public VNCException(String message) {
        super(message);
    }
    
    public VNCException(String message, Throwable cause) {
        super(message, cause);
    }
}
