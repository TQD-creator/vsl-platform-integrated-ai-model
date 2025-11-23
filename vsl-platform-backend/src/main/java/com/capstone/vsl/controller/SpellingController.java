package com.capstone.vsl.controller;

import com.capstone.vsl.dto.ApiResponse;
import com.capstone.vsl.service.SpellingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spelling Controller
 * Handles text-to-spelling conversion (character-by-character hand gestures)
 */
@RestController
@RequestMapping("/api/vsl")
@RequiredArgsConstructor
@Slf4j
public class SpellingController {

    private final SpellingService spellingService;

    /**
     * GET /api/vsl/spell?text=abc
     * Convert text to a list of hand gesture image URLs (character-by-character)
     * 
     * Example:
     * - Input: "abc"
     * - Output: ["https://example.com/gestures/a.png", "https://example.com/gestures/b.png", "https://example.com/gestures/c.png"]
     *
     * @param text Text to spell (will be normalized - accents removed, lowercase)
     * @return List of image URLs for each character
     */
    @GetMapping("/spell")
    public ResponseEntity<ApiResponse<List<String>>> spellText(
            @RequestParam(required = false) String text) {
        try {
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success(
                        "Please provide text to spell",
                        List.of()
                ));
            }

            var imageUrls = spellingService.spellText(text.trim());
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("Spelled '%s' into %d characters", text, imageUrls.size()),
                    imageUrls
            ));
        } catch (Exception e) {
            log.error("Failed to spell text: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "Failed to spell text: " + e.getMessage()));
        }
    }
}

