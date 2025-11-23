package com.capstone.vsl.controller;

import com.capstone.vsl.dto.ApiResponse;
import com.capstone.vsl.dto.DictionaryDTO;
import com.capstone.vsl.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dictionary Controller
 * Handles dictionary search and management endpoints
 */
@RestController
@RequestMapping("/api/dictionary")
@RequiredArgsConstructor
@Slf4j
public class DictionaryController {

    private final DictionaryService dictionaryService;

    /**
     * GET /api/dictionary/search
     * Public endpoint for searching dictionary entries
     * Uses Elasticsearch for fuzzy matching, falls back to PostgreSQL if ES is unavailable
     *
     * @param query Search query string
     * @return List of matching dictionary entries
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DictionaryDTO>>> search(
            @RequestParam(required = false) String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Please provide a search query", List.of()));
            }

            var results = dictionaryService.search(query.trim());
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("Found %d result(s)", results.size()),
                    results
            ));
        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }

    /**
     * POST /api/dictionary
     * Create a new dictionary word (requires ADMIN role)
     * 
     * Note: Regular users should use POST /api/user/contributions to submit words for review.
     * This endpoint is for admins to directly create dictionary entries.
     * 
     * Implements dual-write: PostgreSQL first, then async sync to Elasticsearch
     *
     * @param dto Dictionary data
     * @return Created dictionary entry
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DictionaryDTO>> createWord(@RequestBody DictionaryDTO dto) {
        try {
            var created = dictionaryService.createWord(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Dictionary word created successfully", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create dictionary word: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create dictionary word: " + e.getMessage()));
        }
    }
}

