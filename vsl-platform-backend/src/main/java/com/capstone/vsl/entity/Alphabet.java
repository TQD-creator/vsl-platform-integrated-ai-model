package com.capstone.vsl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alphabet Entity
 * Stores hand gesture images for individual characters (letters and numbers)
 * Used for text-to-spelling feature
 */
@Entity
@Table(name = "alphabet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alphabet {

    @Id
    @Column(length = 1, nullable = false, unique = true)
    private String character;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(nullable = false, length = 10)
    private String type; // "LETTER" or "NUMBER"
}

