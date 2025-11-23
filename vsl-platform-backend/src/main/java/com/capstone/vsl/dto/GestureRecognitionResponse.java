package com.capstone.vsl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO from Gesture Recognition Model (Model 1)
 * Returns raw text without accents (e.g., "coogiaso")
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestureRecognitionResponse {
    private String text; // Raw text without accents
    private String status;
    private String message;
}

