package com.capstone.vsl.controller;

import com.capstone.vsl.dto.ApiResponse;
import com.capstone.vsl.dto.ContributionDTO;
import com.capstone.vsl.dto.ContributionRequest;
import com.capstone.vsl.dto.FavoriteDTO;
import com.capstone.vsl.dto.ReportDTO;
import com.capstone.vsl.dto.ReportRequest;
import com.capstone.vsl.dto.SearchHistoryDTO;
import com.capstone.vsl.security.UserPrincipal;
import com.capstone.vsl.service.ContributionService;
import com.capstone.vsl.service.UserFeatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * User Interaction Controller
 * Handles user interaction features: History, Favorites, Reports
 * 
 * Security: All endpoints require authentication (no guest access)
 * - Class-level @PreAuthorize("isAuthenticated()") ensures GUESTS cannot access
 * - Spring Security will automatically return 401 Unauthorized for unauthenticated requests
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class UserInteractionController {

    private final UserFeatureService userFeatureService;
    private final ContributionService contributionService;

    /**
     * GET /api/user/history
     * Get user's search history
     * Requires authentication (USER or ADMIN role)
     *
     * @param authentication Current authentication (to get username)
     * @return List of search history entries
     */
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SearchHistoryDTO>>> getHistory(Authentication authentication) {
        try {
            var userPrincipal = (UserPrincipal) authentication.getPrincipal();
            var username = userPrincipal.getUsername();

            log.info("Retrieving search history for user: {}", username);
            var history = userFeatureService.getUserSearchHistory(username);

            return ResponseEntity.ok(ApiResponse.success(
                    String.format("Retrieved %d history entries", history.size()),
                    history
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request to get history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to get search history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve search history: " + e.getMessage()));
        }
    }

    /**
     * POST /api/user/favorites/{wordId}
     * Toggle favorite status for a dictionary word
     * If favorite exists, remove it. If not, create it.
     * Requires authentication (USER or ADMIN role)
     *
     * @param wordId Dictionary word ID
     * @param authentication Current authentication (to get username)
     * @return Response indicating if favorite was added or removed
     */
    @PostMapping("/favorites/{wordId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleFavorite(
            @PathVariable Long wordId,
            Authentication authentication) {
        try {
            var userPrincipal = (UserPrincipal) authentication.getPrincipal();
            var username = userPrincipal.getUsername();

            log.info("Toggling favorite for user: {}, wordId: {}", username, wordId);
            var isAdded = userFeatureService.toggleFavorite(wordId, username);

            var message = isAdded ? "Favorite added successfully" : "Favorite removed successfully";
            var data = Map.<String, Object>of(
                    "wordId", wordId,
                    "isFavorite", isAdded,
                    "message", message
            );

            return ResponseEntity.ok(ApiResponse.success(message, data));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request to toggle favorite: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to toggle favorite: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to toggle favorite: " + e.getMessage()));
        }
    }

    /**
     * GET /api/user/favorites
     * Get user's favorites
     * Requires authentication (USER or ADMIN role)
     *
     * @param authentication Current authentication (to get username)
     * @return List of favorite dictionary entries
     */
    @GetMapping("/favorites")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<FavoriteDTO>>> getFavorites(Authentication authentication) {
        try {
            var userPrincipal = (UserPrincipal) authentication.getPrincipal();
            var username = userPrincipal.getUsername();

            log.info("Retrieving favorites for user: {}", username);
            var favorites = userFeatureService.getUserFavorites(username);

            return ResponseEntity.ok(ApiResponse.success(
                    String.format("Retrieved %d favorites", favorites.size()),
                    favorites
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request to get favorites: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to get favorites: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve favorites: " + e.getMessage()));
        }
    }

    /**
     * POST /api/user/reports
     * Create a report for a dictionary word
     * Request Body: { "wordId": 123, "reason": "Wrong video" }
     * Requires authentication (USER or ADMIN role)
     *
     * @param request Report request with wordId and reason
     * @param authentication Current authentication (to get username)
     * @return Created report DTO
     */
    @PostMapping("/reports")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReportDTO>> createReport(
            @Valid @RequestBody ReportRequest request,
            Authentication authentication) {
        try {
            var userPrincipal = (UserPrincipal) authentication.getPrincipal();
            var username = userPrincipal.getUsername();

            log.info("Creating report for user: {}, wordId: {}, reason: {}", 
                    username, request.getWordId(), request.getReason());
            
            var report = userFeatureService.createReport(
                    request.getWordId(),
                    request.getReason(),
                    username
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Report created successfully", report));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request to create report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create report: " + e.getMessage()));
        }
    }

    /**
     * POST /api/user/contributions
     * Create a new contribution for a dictionary word
     * Request Body: { "word": "...", "definition": "...", "videoUrl": "..." }
     * Requires authentication (USER or ADMIN role)
     * 
     * The contribution will be created with PENDING status and requires admin approval
     * before being added to the dictionary.
     *
     * @param request Contribution request with word, definition, and videoUrl
     * @param authentication Current authentication (to get username)
     * @return Created contribution DTO
     */
    @PostMapping("/contributions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ContributionDTO>> createContribution(
            @Valid @RequestBody ContributionRequest request,
            Authentication authentication) {
        try {
            var userPrincipal = (UserPrincipal) authentication.getPrincipal();
            var username = userPrincipal.getUsername();

            log.info("Creating contribution for user: {}, word: {}", username, request.getWord());
            
            var contribution = contributionService.createContribution(request, username);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            "Contribution submitted successfully. It will be reviewed by an admin.",
                            contribution
                    ));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request to create contribution: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create contribution: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create contribution: " + e.getMessage()));
        }
    }
}

