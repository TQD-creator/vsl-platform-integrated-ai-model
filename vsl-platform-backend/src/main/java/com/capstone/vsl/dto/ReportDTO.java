package com.capstone.vsl.dto;

import com.capstone.vsl.entity.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    private Long id;
    private Long dictionaryId;
    private String word;
    private String reason;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

