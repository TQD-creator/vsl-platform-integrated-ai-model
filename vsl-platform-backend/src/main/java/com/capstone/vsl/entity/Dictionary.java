package com.capstone.vsl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dictionary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String word;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "elastic_synced", nullable = false)
    @Builder.Default
    private Boolean elasticSynced = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "dictionary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SearchHistory> searchHistories = new ArrayList<>();

    @OneToMany(mappedBy = "dictionary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserFavorite> favorites = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

