package com.capstone.vsl.repository;

import com.capstone.vsl.document.DictionaryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionarySearchRepository extends ElasticsearchRepository<DictionaryDocument, Long> {
    
    /**
     * Search using Elasticsearch fuzzy matching
     * The Vietnamese analyzer will handle proper text analysis
     */
    List<DictionaryDocument> findByWordContainingIgnoreCaseOrDefinitionContainingIgnoreCase(
            String word, String definition);
}

