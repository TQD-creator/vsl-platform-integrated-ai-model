package com.capstone.vsl.integration.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Immutable DTO representing the complete gesture input from client
 * Contains a list of hand frames (sequence of frames for gesture recognition)
 * and the current text context for accent restoration
 * This is the request body sent from the client
 */
public record GestureInputDTO(
        @NotEmpty(message = "Frames cannot be empty")
        @Valid
        List<HandFrameDTO> frames,
        
        /**
         * Current text context (accumulated text so far)
         * Used by the unified AI service for accent restoration
         * Defaults to empty string if not provided
         */
        String currentText
) {
    /**
     * Constructor with default currentText
     */
    public GestureInputDTO(List<HandFrameDTO> frames) {
        this(frames, "");
    }
}

