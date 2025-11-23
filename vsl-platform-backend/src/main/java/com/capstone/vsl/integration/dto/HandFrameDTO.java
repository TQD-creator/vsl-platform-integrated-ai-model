package com.capstone.vsl.integration.dto;

import java.util.List;

/**
 * Immutable DTO representing a single hand frame with landmarks
 * Contains a list of landmarks for one frame
 */
public record HandFrameDTO(
        List<LandmarkDTO> landmarks
) {
}

