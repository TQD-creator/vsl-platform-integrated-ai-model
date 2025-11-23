package com.capstone.vsl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dictionary_id", nullable = false)
    private Dictionary dictionary;

    @Column(name = "search_query", length = 200)
    private String searchQuery;

    @Column(name = "searched_at", nullable = false, updatable = false)
    private LocalDateTime searchedAt;

    @PrePersist
    protected void onCreate() {
        searchedAt = LocalDateTime.now();
    }
}

