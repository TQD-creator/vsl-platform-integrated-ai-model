package com.capstone.vsl.integration;

import com.capstone.vsl.integration.dto.AiResponseDTO;
import com.capstone.vsl.integration.dto.GestureInputDTO;
import com.capstone.vsl.integration.exception.AiServiceUnavailableException;
import com.capstone.vsl.integration.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Gesture Integration Service
 * Acts as a Gateway/Proxy to the unified Python AI Service
 * 
 * Architecture:
 * - Single API call to unified Python service
 * - Python service handles: Gesture Recognition + Accent Restoration internally
 * - Returns final Vietnamese text with accents
 * 
 * Features:
 * - Robust error handling with timeouts
 * - Comprehensive logging
 * - Simple gateway pattern (no orchestration logic)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GestureIntegrationService {

    @Qualifier("aiRestClient")
    private final RestClient aiRestClient;

    /**
     * Process gesture input through the unified AI pipeline
     * 
     * Pipeline (handled by Python service):
     * 1. Validate input
     * 2. Process landmarks -> Recognize gesture
     * 3. Apply accent restoration to current_text + new character
     * 4. Return final Vietnamese text with accents
     *
     * @param input Gesture input with landmarks and current text context
     * @return Final corrected Vietnamese text
     * @throws IllegalArgumentException if input is invalid
     * @throws AiServiceUnavailableException if AI service is offline
     * @throws ExternalServiceException if external service returns error
     */
    public String processGesture(GestureInputDTO input) {
        // Validation
        if (input.frames() == null || input.frames().isEmpty()) {
            throw new IllegalArgumentException("Frames cannot be empty");
        }

        var frameCount = input.frames().size();
        var currentText = input.currentText() != null ? input.currentText() : "";
        log.info("Received gesture request with [{}] frames, current_text: '{}'", frameCount, currentText);

        // Prepare request body matching Python API format
        var requestBody = Map.of(
                "frames", input.frames(),
                "current_text", currentText
        );

        try {
            log.debug("Calling unified AI service with {} frames", frameCount);

            ResponseEntity<AiResponseDTO> response = aiRestClient.post()
                    .uri("")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(AiResponseDTO.class);

            var responseBody = response.getBody();
            
            // Validate response
            if (responseBody == null) {
                throw new ExternalServiceException("AI Service returned null response", 
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            // Check if request was successful
            if (Boolean.FALSE.equals(responseBody.success()) || responseBody.error() != null) {
                var errorMsg = responseBody.error() != null 
                        ? responseBody.error() 
                        : "AI Service returned unsuccessful response";
                log.error("AI Service error: {}", errorMsg);
                throw new ExternalServiceException("AI Service error: " + errorMsg, 
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            // Extract final sentence
            if (responseBody.finalSentence() == null || responseBody.finalSentence().trim().isEmpty()) {
                throw new ExternalServiceException("AI Service returned empty final_sentence", 
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            var finalSentence = responseBody.finalSentence().trim();
            log.info("Unified AI service returned: '{}' (confidence: {}, raw_char: '{}')", 
                    finalSentence, 
                    responseBody.confidence(), 
                    responseBody.rawChar());

            return finalSentence;

        } catch (ResourceAccessException e) {
            log.error("AI Service is unavailable: {}", e.getMessage());
            throw new AiServiceUnavailableException("AI Service is offline", e);
        } catch (HttpServerErrorException e) {
            log.error("AI Service returned server error: {} - {}", 
                    e.getStatusCode(), e.getMessage());
            throw new ExternalServiceException(
                    "AI Service error: " + e.getStatusCode(), 
                    e.getStatusCode().value(), 
                    e);
        } catch (ExternalServiceException e) {
            // Re-throw as-is
            throw e;
        } catch (Exception e) {
            log.error("Failed to call AI Service: {}", e.getMessage(), e);
            throw new ExternalServiceException(
                    "Failed to process gesture recognition: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e);
        }
    }
}
