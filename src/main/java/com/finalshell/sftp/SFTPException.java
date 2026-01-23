package com.finalshell.sftp;

/**
 * SFTP Exception - Custom exception for SFTP operations
 */
public class SFTPException extends Exception {
    
    public SFTPException(String message) {
        super(message);
    }
    
    public SFTPException(String message, Throwable cause) {
        super(message, cause);
    }
}
