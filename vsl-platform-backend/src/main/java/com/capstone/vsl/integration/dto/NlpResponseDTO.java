package com.capstone.vsl.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable DTO representing the response from Python Model 2 (NLP/Accent Correction)
 * Expected JSON format: {"text": "trường học"}
 */
public record NlpResponseDTO(
        @JsonProperty("text")
        String text
) {
}

