package com.capstone.vsl.dto;

import com.capstone.vsl.entity.ContributionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Contribution information (for admin dashboard)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributionDTO {
    private Long id;
    private Long userId;
    private String username;
    private String stagingData;
    private ContributionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

