package com.capstone.vsl.integration.exception;

/**
 * Exception thrown when Gesture Recognition Model (Model 1) fails
 */
public class GestureRecognitionException extends PythonServiceException {
    
    public GestureRecognitionException(String message) {
        super(message);
    }
    
    public GestureRecognitionException(String message, Throwable cause) {
        super(message, cause);
    }
}

