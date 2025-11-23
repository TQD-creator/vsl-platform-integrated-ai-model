package com.capstone.vsl.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DictionaryDTO {
    
    private Long id;
    
    @NotBlank(message = "Word is required")
    private String word;
    
    private String definition;
    
    @NotBlank(message = "Video URL is required")
    private String videoUrl;
    
    private Boolean elasticSynced;
}

