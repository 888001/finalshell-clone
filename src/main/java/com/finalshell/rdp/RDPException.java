package com.finalshell.rdp;

/**
 * RDP Exception
 */
public class RDPException extends Exception {
    
    public RDPException(String message) {
        super(message);
    }
    
    public RDPException(String message, Throwable cause) {
        super(message, cause);
    }
}
