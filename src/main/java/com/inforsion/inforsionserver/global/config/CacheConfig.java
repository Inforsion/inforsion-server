package com.inforsion.inforsionserver.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(Arrays.asList("ocrJobStatus", "receiptAnalysis", "ocrResults"));
        
        log.info("캐시 매니저 초기화 완료 - 캐시명: ocrJobStatus, receiptAnalysis, ocrResults");
        return cacheManager;
    }
}