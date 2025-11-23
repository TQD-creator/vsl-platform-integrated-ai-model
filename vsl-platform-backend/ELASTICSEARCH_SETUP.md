# Elasticsearch Vietnamese Analyzer Setup

This document explains how to set up the Vietnamese analyzer with ICU folding for the VSL Platform dictionary search.

## Prerequisites

1. Elasticsearch 8.x running on `localhost:9200`
2. ICU Analysis plugin installed in Elasticsearch

## Install ICU Analysis Plugin

```bash
# Navigate to Elasticsearch installation directory
cd /path/to/elasticsearch

# Install ICU Analysis plugin
bin/elasticsearch-plugin install analysis-icu
```

After installation, restart Elasticsearch.

## Create Index with Vietnamese Analyzer

Use the following command to create the `dictionary` index with Vietnamese analyzer:

```bash
curl -X PUT "localhost:9200/dictionary" -H 'Content-Type: application/json' -d'
{
  "settings": {
    "analysis": {
      "analyzer": {
        "vietnamese_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "icu_folding"
          ]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "id": {
        "type": "long"
      },
      "word": {
        "type": "text",
        "analyzer": "vietnamese_analyzer",
        "search_analyzer": "vietnamese_analyzer"
      },
      "definition": {
        "type": "text",
        "analyzer": "vietnamese_analyzer",
        "search_analyzer": "vietnamese_analyzer"
      },
      "videoUrl": {
        "type": "keyword"
      },
      "elasticSynced": {
        "type": "boolean"
      }
    }
  }
}'
```

## Verify Index Creation

```bash
curl -X GET "localhost:9200/dictionary/_settings?pretty"
```

## Test Vietnamese Analyzer

```bash
curl -X GET "localhost:9200/dictionary/_analyze?pretty" -H 'Content-Type: application/json' -d'
{
  "analyzer": "vietnamese_analyzer",
  "text": "Xin chào"
}'
```

## Notes

- The `icu_folding` filter handles Vietnamese diacritics (ă, â, đ, ê, ô, ơ, ư) properly
- The analyzer will normalize Vietnamese text for better search results
- If Elasticsearch is unavailable, the system automatically falls back to PostgreSQL ILIKE search

