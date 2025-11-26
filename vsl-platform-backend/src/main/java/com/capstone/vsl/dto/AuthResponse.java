package com.capstone.vsl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private String username;
    private String email;
    private String role;
    
    // Extended profile fields (optional)
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private String bio;
    private String address;
}

