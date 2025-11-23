package com.capstone.vsl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Search History
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistoryDTO {
    private Long id;
    private Long dictionaryId;
    private String word;
    private String searchQuery;
    private LocalDateTime searchedAt;
}

