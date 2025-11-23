package com.capstone.vsl.repository;

import com.capstone.vsl.entity.Alphabet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlphabetRepository extends JpaRepository<Alphabet, String> {
    
    /**
     * Find all alphabets by their character IDs
     * Optimized batch query for fetching multiple characters at once
     */
    List<Alphabet> findAllById(Iterable<String> characters);
}

