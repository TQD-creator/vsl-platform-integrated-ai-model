package com.capstone.vsl.integration;

import com.capstone.vsl.dto.AccentCorrectionRequest;
import com.capstone.vsl.dto.AccentCorrectionResponse;
import com.capstone.vsl.dto.GestureRecognitionResponse;
import com.capstone.vsl.dto.GestureToTextResponse;
import com.capstone.vsl.integration.exception.AccentCorrectionException;
import com.capstone.vsl.integration.exception.GestureRecognitionException;
import com.capstone.vsl.integration.exception.PythonServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Python Integration Service
 * Handles communication with external Python AI models:
 * - Model 1: Gesture Recognition (localhost:5000)
 * - Model 2: Accent Correction (localhost:5001)
 * 
 * Uses Java 21 RestClient for HTTP communication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PythonIntegrationService {

    @Value("${python.model1.url:http://localhost:5000}")
    private String model1BaseUrl;

    @Value("${python.model2.url:http://localhost:5001}")
    private String model2BaseUrl;

    @Value("${python.model1.timeout:30}")
    private int model1TimeoutSeconds;

    @Value("${python.model2.timeout:10}")
    private int model2TimeoutSeconds;

    private final RestClient.Builder restClientBuilder;

    /**
     * Create RestClient for Model 1 with timeout
     */
    private RestClient createModel1Client() {
        return restClientBuilder
                .baseUrl(model1BaseUrl)
                .build();
    }

    /**
     * Create RestClient for Model 2 with timeout
     */
    private RestClient createModel2Client() {
        return restClientBuilder
                .baseUrl(model2BaseUrl)
                .build();
    }

    /**
     * Process gesture video to Vietnamese text
     * Pipeline: Video -> Model 1 (Gesture Recognition) -> Model 2 (Accent Correction) -> Final Text
     *
     * @param videoFile Video file to process
     * @return GestureToTextResponse with corrected Vietnamese text
     * @throws GestureRecognitionException if Model 1 fails
     * @throws AccentCorrectionException if Model 2 fails
     * @throws PythonServiceException if service is unavailable
     */
    public GestureToTextResponse processGestureToText(MultipartFile videoFile) {
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("Video file is required");
        }

        log.info("Processing gesture video: {} ({} bytes)", 
                videoFile.getOriginalFilename(), videoFile.getSize());

        try {
            // Step A: Send video to Model 1 (Gesture Recognition)
            var rawText = callGestureRecognitionModel(videoFile);
            log.info("Model 1 returned raw text: {}", rawText);

            // Step B: Send raw text to Model 2 (Accent Correction)
            var correctedText = callAccentCorrectionModel(rawText);
            log.info("Model 2 returned corrected text: {}", correctedText);

            // Step C: Return final result
            return GestureToTextResponse.builder()
                    .rawText(rawText)
                    .correctedText(correctedText)
                    .status("success")
                    .message("Gesture recognition completed successfully")
                    .build();

        } catch (GestureRecognitionException | AccentCorrectionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in gesture-to-text pipeline: {}", e.getMessage(), e);
            throw new PythonServiceException("Failed to process gesture video: " + e.getMessage(), e);
        }
    }

    /**
     * Step A: Call Gesture Recognition Model (Model 1)
     * POST http://localhost:5000/predict-gesture
     * Input: Video file (MultipartFile)
     * Output: Raw text without accents
     *
     * @param videoFile Video file to analyze
     * @return Raw text from gesture recognition
     * @throws GestureRecognitionException if model fails or is unavailable
     */
    private String callGestureRecognitionModel(MultipartFile videoFile) {
        var restClient = createModel1Client();

        try {
            log.debug("Calling Gesture Recognition Model at: {}/predict-gesture", model1BaseUrl);

            // Prepare multipart request
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            var body = new LinkedMultiValueMap<String, Object>();
            var fileResource = new MultipartFileResource(videoFile);
            body.add("file", fileResource);

            // Call Model 1
            var requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<GestureRecognitionResponse> response = restClient.post()
                    .uri("/predict-gesture")
                    .body(requestEntity)
                    .retrieve()
                    .toEntity(GestureRecognitionResponse.class);

            var responseBody = response.getBody();
            if (responseBody == null || responseBody.getText() == null || responseBody.getText().trim().isEmpty()) {
                throw new GestureRecognitionException("Model 1 returned empty or invalid response");
            }

            log.debug("Model 1 response: {}", responseBody);
            return responseBody.getText().trim();

        } catch (ResourceAccessException e) {
            log.error("Model 1 is unavailable: {}", e.getMessage());
            throw new GestureRecognitionException(
                    "Gesture Recognition Model is unavailable. Please check if the service is running on " + model1BaseUrl, e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Model 1 returned error: {} - {}", e.getStatusCode(), e.getMessage());
            throw new GestureRecognitionException(
                    "Gesture Recognition Model returned error: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to call Gesture Recognition Model: {}", e.getMessage(), e);
            throw new GestureRecognitionException("Failed to process gesture recognition: " + e.getMessage(), e);
        }
    }

    /**
     * Step B: Call Accent Correction Model (Model 2)
     * POST http://localhost:5001/add-accents
     * Input: Raw text without accents
     * Output: Corrected Vietnamese text with accents
     *
     * @param rawText Raw text without accents (e.g., "coogiaso")
     * @return Corrected text with accents (e.g., "cô giáo")
     * @throws AccentCorrectionException if model fails or is unavailable
     */
    private String callAccentCorrectionModel(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            throw new IllegalArgumentException("Raw text cannot be empty");
        }

        var restClient = createModel2Client();

        try {
            log.debug("Calling Accent Correction Model at: {}/add-accents with text: {}", model2BaseUrl, rawText);

            var request = new AccentCorrectionRequest(rawText);

            // Call Model 2
            ResponseEntity<AccentCorrectionResponse> response = restClient.post()
                    .uri("/add-accents")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(AccentCorrectionResponse.class);

            var responseBody = response.getBody();
            if (responseBody == null || responseBody.getText() == null || responseBody.getText().trim().isEmpty()) {
                throw new AccentCorrectionException("Model 2 returned empty or invalid response");
            }

            log.debug("Model 2 response: {}", responseBody);
            return responseBody.getText().trim();

        } catch (ResourceAccessException e) {
            log.error("Model 2 is unavailable: {}", e.getMessage());
            throw new AccentCorrectionException(
                    "Accent Correction Model is unavailable. Please check if the service is running on " + model2BaseUrl, e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Model 2 returned error: {} - {}", e.getStatusCode(), e.getMessage());
            throw new AccentCorrectionException(
                    "Accent Correction Model returned error: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to call Accent Correction Model: {}", e.getMessage(), e);
            throw new AccentCorrectionException("Failed to correct accents: " + e.getMessage(), e);
        }
    }

    /**
     * Helper class to convert MultipartFile to Resource for RestClient
     */
    private static class MultipartFileResource implements org.springframework.core.io.Resource {
        private final MultipartFile file;

        public MultipartFileResource(MultipartFile file) {
            this.file = file;
        }

        @Override
        public java.io.InputStream getInputStream() throws IOException {
            return file.getInputStream();
        }

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public String getFilename() {
            return file.getOriginalFilename();
        }

        @Override
        public long contentLength() throws IOException {
            return file.getSize();
        }

        @Override
        public Resource createRelative(String relativePath) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getDescription() {
            return "MultipartFile resource: " + file.getOriginalFilename();
        }

        @Override
        public java.net.URI getURI() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.net.URL getURL() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.io.File getFile() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long lastModified() throws IOException {
            return 0;
        }
    }
}

