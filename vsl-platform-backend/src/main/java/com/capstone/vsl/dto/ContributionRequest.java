package com.capstone.vsl.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new contribution
 * Used when users submit new dictionary words for admin review
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributionRequest {
    
    @NotBlank(message = "Word is required")
    private String word;
    
    private String definition;
    
    @NotBlank(message = "Video URL is required")
    private String videoUrl;
}

