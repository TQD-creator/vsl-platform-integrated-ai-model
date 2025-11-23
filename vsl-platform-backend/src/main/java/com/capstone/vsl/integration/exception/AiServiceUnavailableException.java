package com.capstone.vsl.integration.exception;

/**
 * Exception thrown when AI service is unavailable (connection refused, timeout, etc.)
 */
public class AiServiceUnavailableException extends RuntimeException {
    
    public AiServiceUnavailableException(String message) {
        super(message);
    }
    
    public AiServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

