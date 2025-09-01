package com.inforsion.inforsionserver.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "ocrTaskExecutor")
    public Executor ocrTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // OCR 작업용 스레드 풀 설정
        executor.setCorePoolSize(4);        // 기본 스레드 수
        executor.setMaxPoolSize(8);         // 최대 스레드 수
        executor.setQueueCapacity(100);     // 대기 큐 크기
        executor.setKeepAliveSeconds(60);   // 유휴 스레드 생존 시간
        
        executor.setThreadNamePrefix("OCR-Task-");
        executor.setRejectedExecutionHandler((runnable, executor1) -> {
            log.warn("OCR 작업 스레드 풀이 포화상태입니다. 작업이 거부되었습니다.");
            throw new RuntimeException("OCR 작업 스레드 풀 포화");
        });
        
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        log.info("OCR Task Executor 초기화 완료 - CoreSize: {}, MaxSize: {}, QueueCapacity: {}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
    
    @Bean(name = "fileProcessingExecutor")
    public Executor fileProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 파일 처리용 스레드 풀 설정 (더 많은 스레드 할당)
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        
        executor.setThreadNamePrefix("File-Processing-");
        executor.setRejectedExecutionHandler((runnable, executor1) -> {
            log.warn("파일 처리 스레드 풀이 포화상태입니다.");
            throw new RuntimeException("파일 처리 스레드 풀 포화");
        });
        
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        log.info("File Processing Executor 초기화 완료 - CoreSize: {}, MaxSize: {}, QueueCapacity: {}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
}