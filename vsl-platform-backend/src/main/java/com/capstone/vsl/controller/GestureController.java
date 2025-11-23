package com.capstone.vsl.controller;

import com.capstone.vsl.dto.ApiResponse;
import com.capstone.vsl.dto.GestureToTextResponse;
import com.capstone.vsl.integration.PythonIntegrationService;
import com.capstone.vsl.integration.exception.AccentCorrectionException;
import com.capstone.vsl.integration.exception.GestureRecognitionException;
import com.capstone.vsl.integration.exception.PythonServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Gesture Recognition Controller
 * Handles gesture-to-text conversion using Python AI models
 */
@RestController
@RequestMapping("/api/gesture")
@RequiredArgsConstructor
@Slf4j
public class GestureController {

    private final PythonIntegrationService pythonIntegrationService;

    /**
     * POST /api/gesture/process
     * Process gesture video to Vietnamese text
     * Pipeline: Video -> Gesture Recognition -> Accent Correction -> Final Text
     *
     * @param videoFile Video file containing gesture
     * @return Corrected Vietnamese text
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<GestureToTextResponse>> processGesture(
            @RequestParam("file") MultipartFile videoFile) {
        try {
            if (videoFile == null || videoFile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Video file is required"));
            }

            var result = pythonIntegrationService.processGestureToText(videoFile);
            return ResponseEntity.ok(ApiResponse.success("Gesture processed successfully", result));

        } catch (GestureRecognitionException e) {
            log.error("Gesture recognition failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("Gesture Recognition Model error: " + e.getMessage()));
        } catch (AccentCorrectionException e) {
            log.error("Accent correction failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("Accent Correction Model error: " + e.getMessage()));
        } catch (PythonServiceException e) {
            log.error("Python service error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("AI service error: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error processing gesture: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process gesture: " + e.getMessage()));
        }
    }
}

