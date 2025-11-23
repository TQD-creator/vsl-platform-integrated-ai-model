package com.capstone.vsl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO from Accent Correction Model (Model 2)
 * Returns corrected Vietnamese text with accents (e.g., "cô giáo")
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccentCorrectionResponse {
    private String text; // Corrected text with accents
    private String status;
    private String message;
}

