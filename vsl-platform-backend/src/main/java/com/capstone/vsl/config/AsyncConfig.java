package com.capstone.vsl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async Configuration
 * Configures thread pool for asynchronous Elasticsearch synchronization
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "elasticsearchSyncExecutor")
    public Executor elasticsearchSyncExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("es-sync-");
        executor.initialize();
        return executor;
    }
}

