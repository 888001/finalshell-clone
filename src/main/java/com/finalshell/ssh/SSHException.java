package com.finalshell.ssh;

/**
 * SSH Exception - Custom exception for SSH operations
 */
public class SSHException extends Exception {
    
    public SSHException(String message) {
        super(message);
    }
    
    public SSHException(String message, Throwable cause) {
        super(message, cause);
    }
}
