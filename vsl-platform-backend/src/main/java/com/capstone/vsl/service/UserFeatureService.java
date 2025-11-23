package com.capstone.vsl.service;

import com.capstone.vsl.dto.FavoriteDTO;
import com.capstone.vsl.dto.ReportDTO;
import com.capstone.vsl.dto.SearchHistoryDTO;
import com.capstone.vsl.entity.Report;
import com.capstone.vsl.entity.ReportStatus;
import com.capstone.vsl.entity.SearchHistory;
import com.capstone.vsl.entity.UserFavorite;
import com.capstone.vsl.repository.DictionaryRepository;
import com.capstone.vsl.repository.ReportRepository;
import com.capstone.vsl.repository.SearchHistoryRepository;
import com.capstone.vsl.repository.UserFavoriteRepository;
import com.capstone.vsl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Feature Service
 * Handles user interaction features:
 * - Search History logging
 * - Favorites management
 * - Report creation
 * 
 * Important: All methods require authenticated users (no guest access)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserFeatureService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserFavoriteRepository userFavoriteRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final DictionaryRepository dictionaryRepository;

    /**
     * Log search history
     * Important: If username is null or anonymous, DO NOTHING (return void).
     * Do not throw exception, just ignore.
     *
     * @param keyword Search keyword
     * @param dictionaryId Dictionary entry ID that was found
     * @param username Username of the user (null/empty for guests)
     */
    @Transactional
    public void logSearchHistory(String keyword, Long dictionaryId, String username) {
        // If username is null or anonymous, DO NOTHING (return void)
        if (username == null || username.trim().isEmpty() || username.equalsIgnoreCase("anonymous")) {
            log.debug("Skipping search history log for guest/anonymous user");
            return;
        }

        try {
            var user = userRepository.findByUsername(username)
                    .orElse(null);
            
            if (user == null) {
                log.debug("User not found for search history: {}", username);
                return;
            }

            var dictionary = dictionaryRepository.findById(dictionaryId)
                    .orElse(null);
            
            if (dictionary == null) {
                log.debug("Dictionary not found for search history: {}", dictionaryId);
                return;
            }

            var searchHistory = SearchHistory.builder()
                    .user(user)
                    .dictionary(dictionary)
                    .searchQuery(keyword)
                    .build();

            searchHistoryRepository.save(searchHistory);
            log.debug("Logged search history: user={}, keyword={}, dictionaryId={}", 
                    username, keyword, dictionaryId);

        } catch (Exception e) {
            // Silently handle errors - don't break the main flow
            log.warn("Failed to log search history: {}", e.getMessage());
        }
    }

    /**
     * Get user's search history
     *
     * @param username Username of the authenticated user
     * @return List of search history entries
     */
    @Transactional(readOnly = true)
    public List<SearchHistoryDTO> getUserSearchHistory(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        var history = searchHistoryRepository.findByUserOrderBySearchedAtDesc(user);
        log.debug("Retrieved {} search history entries for user: {}", history.size(), username);

        return history.stream()
                .map(this::searchHistoryToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Toggle favorite status for a dictionary word
     * If favorite exists, remove it. If not, create it.
     *
     * @param wordId Dictionary word ID
     * @param username Username of the authenticated user
     * @return true if added, false if removed
     */
    @Transactional
    public boolean toggleFavorite(Long wordId, String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        var dictionary = dictionaryRepository.findById(wordId)
                .orElseThrow(() -> new IllegalArgumentException("Dictionary word not found: " + wordId));

        var existingFavorite = userFavoriteRepository.findByUserAndDictionary(user, dictionary);

        if (existingFavorite.isPresent()) {
            // Remove favorite
            userFavoriteRepository.delete(existingFavorite.get());
            log.info("Removed favorite: user={}, wordId={}", username, wordId);
            return false;
        } else {
            // Add favorite
            var favorite = UserFavorite.builder()
                    .user(user)
                    .dictionary(dictionary)
                    .build();
            userFavoriteRepository.save(favorite);
            log.info("Added favorite: user={}, wordId={}", username, wordId);
            return true;
        }
    }

    /**
     * Get user's favorites
     *
     * @param username Username of the authenticated user
     * @return List of favorite dictionary entries
     */
    @Transactional(readOnly = true)
    public List<FavoriteDTO> getUserFavorites(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        var favorites = userFavoriteRepository.findByUserOrderByCreatedAtDesc(user);
        log.debug("Retrieved {} favorites for user: {}", favorites.size(), username);

        return favorites.stream()
                .map(this::favoriteToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a report for a dictionary word
     *
     * @param wordId Dictionary word ID
     * @param reason Report reason
     * @param username Username of the authenticated user
     * @return Created report DTO
     */
    @Transactional
    public ReportDTO createReport(Long wordId, String reason, String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        var dictionary = dictionaryRepository.findById(wordId)
                .orElseThrow(() -> new IllegalArgumentException("Dictionary word not found: " + wordId));

        var report = Report.builder()
                .user(user)
                .dictionary(dictionary)
                .reason(reason)
                .status(ReportStatus.OPEN)
                .build();

        report = reportRepository.save(report);
        log.info("Created report: user={}, wordId={}, reason={}", username, wordId, reason);

        return reportToDTO(report);
    }

    /**
     * Convert SearchHistory entity to DTO
     */
    private SearchHistoryDTO searchHistoryToDTO(SearchHistory history) {
        return SearchHistoryDTO.builder()
                .id(history.getId())
                .dictionaryId(history.getDictionary().getId())
                .word(history.getDictionary().getWord())
                .searchQuery(history.getSearchQuery())
                .searchedAt(history.getSearchedAt())
                .build();
    }

    /**
     * Convert UserFavorite entity to DTO
     */
    private FavoriteDTO favoriteToDTO(UserFavorite favorite) {
        var dictionary = favorite.getDictionary();
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .dictionaryId(dictionary.getId())
                .word(dictionary.getWord())
                .definition(dictionary.getDefinition())
                .videoUrl(dictionary.getVideoUrl())
                .createdAt(favorite.getCreatedAt())
                .build();
    }

    /**
     * Convert Report entity to DTO
     */
    private ReportDTO reportToDTO(Report report) {
        return ReportDTO.builder()
                .id(report.getId())
                .dictionaryId(report.getDictionary().getId())
                .word(report.getDictionary().getWord())
                .reason(report.getReason())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}

