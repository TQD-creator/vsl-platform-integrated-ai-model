package com.capstone.vsl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API Response Wrapper
 * Ensures all responses (success or error) follow a consistent format for Next.js compatibility
 * 
 * @param <T> Type of the data payload
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    
    /**
     * Create a successful response with HTTP 200
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("Operation successful")
                .data(data)
                .build();
    }
    
    /**
     * Create a successful response with HTTP 200 and custom message
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * Create an error response with HTTP 400
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code(400)
                .message(message)
                .data(null)
                .build();
    }
    
    /**
     * Create an error response with custom HTTP code
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}

