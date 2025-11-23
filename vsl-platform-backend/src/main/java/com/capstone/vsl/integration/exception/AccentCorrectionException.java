package com.capstone.vsl.integration.exception;

/**
 * Exception thrown when Accent Correction Model (Model 2) fails
 */
public class AccentCorrectionException extends PythonServiceException {
    
    public AccentCorrectionException(String message) {
        super(message);
    }
    
    public AccentCorrectionException(String message, Throwable cause) {
        super(message, cause);
    }
}

