package com.capstone.vsl.integration.exception;

/**
 * Exception thrown when external service returns an error (5xx, etc.)
 */
public class ExternalServiceException extends RuntimeException {
    
    private final int statusCode;
    
    public ExternalServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public ExternalServiceException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}

