package com.capstone.vsl.service;

import com.capstone.vsl.repository.AlphabetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Spelling Service
 * Converts text to a list of hand gesture image URLs (character-by-character)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpellingService {

    private final AlphabetRepository alphabetRepository;
    
    private static final String SPACE_PLACEHOLDER = "https://placehold.co/100x100?text=Space";
    private static final String UNKNOWN_PLACEHOLDER = "https://example.com/gestures/unknown.png";

    /**
     * Spell text character-by-character
     * Returns a list of image URLs for each character in the text
     *
     * @param text Input text to spell
     * @return List of image URLs (one per character)
     */
    @Transactional(readOnly = true)
    public List<String> spellText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        log.debug("Spelling text: {}", text);

        // Step 1: Convert to lowercase
        var lowerText = text.toLowerCase();
        log.debug("Lowercase: {}", lowerText);

        // Step 2: Normalize text to remove accents (e.g., "xin chào" -> "xin chao")
        var normalizedText = normalizeText(lowerText);
        log.debug("Normalized: {}", normalizedText);

        // Step 3: Extract unique characters for batch query (optimization)
        var uniqueChars = normalizedText.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .filter(c -> !c.equals(" ")) // Exclude spaces
                .distinct()
                .collect(Collectors.toSet());

        // Step 4: Fetch all needed characters in one query (optimization)
        var alphabetMap = new HashMap<String, String>();
        if (!uniqueChars.isEmpty()) {
            var alphabets = alphabetRepository.findAllById(uniqueChars);
            alphabets.forEach(alpha -> alphabetMap.put(alpha.getCharacter(), alpha.getImageUrl()));
            log.debug("Fetched {} alphabet entries", alphabets.size());
        }

        // Step 5: Build result list
        var result = new ArrayList<String>();
        for (char c : normalizedText.toCharArray()) {
            var charStr = String.valueOf(c);
            
            if (charStr.equals(" ")) {
                // Step 5a: Handle space
                result.add(SPACE_PLACEHOLDER);
            } else {
                // Step 5b: Get image URL from map or use default
                var imageUrl = alphabetMap.getOrDefault(charStr, UNKNOWN_PLACEHOLDER);
                result.add(imageUrl);
            }
        }

        log.info("Spelled text '{}' into {} image URLs", text, result.size());
        return result;
    }

    /**
     * Normalize text to remove Vietnamese accents
     * Example: "xin chào" -> "xin chao"
     *
     * @param text Input text with possible accents
     * @return Normalized text without accents
     */
    private String normalizeText(String text) {
        // Normalize to NFD (Canonical Decomposition)
        var normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        
        // Remove diacritical marks (accents)
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}

