package com.capstone.vsl.integration.dto;

/**
 * Immutable DTO representing a single landmark point (x, y, z coordinates)
 * Used for hand gesture recognition
 */
public record LandmarkDTO(
        float x,
        float y,
        float z
) {
}

