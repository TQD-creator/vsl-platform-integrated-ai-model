package com.capstone.vsl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Final response DTO for the complete gesture-to-text pipeline
 * Contains the final corrected Vietnamese text
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GestureToTextResponse {
    private String rawText; // Text from Model 1 (without accents)
    private String correctedText; // Text from Model 2 (with accents)
    private String status;
    private String message;
}

