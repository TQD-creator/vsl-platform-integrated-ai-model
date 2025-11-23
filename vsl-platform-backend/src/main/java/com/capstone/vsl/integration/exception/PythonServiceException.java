package com.capstone.vsl.integration.exception;

/**
 * Custom exception for Python AI service integration errors
 */
public class PythonServiceException extends RuntimeException {
    
    public PythonServiceException(String message) {
        super(message);
    }
    
    public PythonServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

