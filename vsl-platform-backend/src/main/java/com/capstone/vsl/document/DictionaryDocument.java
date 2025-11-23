package com.capstone.vsl.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * Dictionary Document for Elasticsearch
 * Uses Vietnamese Analyzer (ICU folding) for proper Vietnamese text search.
 */
@Document(indexName = "dictionary")
@Setting(settingPath = "/es-settings.json")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DictionaryDocument {

    @Id
    private Long id;

    @Field(
        type = FieldType.Text,
        analyzer = "vietnamese_analyzer",
        searchAnalyzer = "vietnamese_analyzer"
    )
    private String word;

    @Field(
        type = FieldType.Text,
        analyzer = "vietnamese_analyzer",
        searchAnalyzer = "vietnamese_analyzer"
    )
    private String definition;

    @Field(type = FieldType.Keyword)
    private String videoUrl;

    @Field(type = FieldType.Boolean)
    private Boolean elasticSynced;
}

