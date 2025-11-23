package com.capstone.vsl.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    
    @NotNull(message = "Word ID is required")
    private Long wordId;
    
    @NotBlank(message = "Reason is required")
    private String reason;
}

