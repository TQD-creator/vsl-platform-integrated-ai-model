package com.capstone.vsl.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable DTO representing the response from Python Model 1 (Gesture Recognition)
 * Expected JSON format: {"result": "truonghoc", "confidence": 0.95}
 */
public record PythonResponseDTO(
        @JsonProperty("result")
        String result,
        
        @JsonProperty("confidence")
        double confidence
) {
}

