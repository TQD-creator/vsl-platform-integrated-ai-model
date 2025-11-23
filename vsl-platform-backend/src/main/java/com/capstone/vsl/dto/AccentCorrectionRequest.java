package com.capstone.vsl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Accent Correction Model (Model 2)
 * Input: Raw text without accents
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccentCorrectionRequest {
    private String text; // Raw text without accents (e.g., "coogiaso")
}

