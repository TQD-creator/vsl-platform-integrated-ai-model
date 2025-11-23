package com.capstone.vsl.dto;

import com.capstone.vsl.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating user role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {
    @NotNull(message = "Role is required")
    private Role role;
}

