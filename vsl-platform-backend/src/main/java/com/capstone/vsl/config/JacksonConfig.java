package com.capstone.vsl.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson Configuration
 * Configures ObjectMapper for Next.js compatibility:
 * - Ignore unknown properties (prevents crashes if Frontend sends extra fields)
 * - Write dates as ISO-8601 strings (not timestamps)
 * - Register JavaTimeModule for LocalDateTime support
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        var mapper = new ObjectMapper();
        
        // Ignore unknown properties (prevents crashes if Frontend sends extra fields)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Write dates as ISO-8601 strings (not timestamps) for Next.js compatibility
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        // Register JavaTimeModule for LocalDateTime, LocalDate, etc. support
        mapper.registerModule(new JavaTimeModule());
        
        return mapper;
    }
}

