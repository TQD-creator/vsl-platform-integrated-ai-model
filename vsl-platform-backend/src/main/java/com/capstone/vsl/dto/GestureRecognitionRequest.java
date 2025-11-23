package com.capstone.vsl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Gesture Recognition Model (Model 1)
 * Accepts video URL or file
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestureRecognitionRequest {
    private String videoUrl;
    // Note: For file upload, we'll use MultipartFile directly
}

