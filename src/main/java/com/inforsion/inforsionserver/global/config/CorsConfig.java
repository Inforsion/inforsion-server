package com.inforsion.inforsionserver.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) 설정
 * React Native 앱과의 통신을 위한 CORS 정책 구성
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:8081,http://localhost:19006}")
    private List<String> allowedOrigins;

    @Value("${cors.allowed-origins-patterns:}")
    private List<String> allowedOriginPatterns;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * CORS 설정을 위한 CorsConfigurationSource Bean
     * React Native 개발 환경과 운영 환경을 모두 고려한 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 개발 환경과 운영 환경별 Origin 설정
        if ("dev".equals(activeProfile) || "local".equals(activeProfile)) {
            // 개발 환경: 더 관대한 CORS 설정
            configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",           // Metro bundler (React Native)
                "http://127.0.0.1:*",          // 로컬 개발
                "http://10.0.2.2:*",           // Android Emulator
                "http://192.168.*.*:*",        // 로컬 네트워크
                "exp://*",                     // Expo 개발 서버
                "exp://192.168.*.*:*"          // Expo 로컬 네트워크
            ));
            configuration.setAllowCredentials(true);
        } else {
            // 운영 환경: 보안을 고려한 제한적 설정
            configuration.setAllowedOrigins(allowedOrigins);
            if (!allowedOriginPatterns.isEmpty()) {
                configuration.setAllowedOriginPatterns(allowedOriginPatterns);
            }
            configuration.setAllowCredentials(true);
        }

        // React Native에서 사용하는 HTTP 메서드 허용
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));

        // React Native에서 필요한 헤더 허용
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-Api-Key",
            "X-Device-Id",
            "X-Platform",           // iOS/Android 플랫폼 구분
            "User-Agent"
        ));

        // 브라우저에 노출할 헤더 설정
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization",
            "Content-Disposition",
            "X-Total-Count",        // 페이징 정보
            "X-Page-Number",
            "X-Page-Size"
        ));

        // Preflight 요청 캐시 시간 (초)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // API 경로에만 CORS 적용
        source.registerCorsConfiguration("/api/**", configuration);
        
        // Swagger UI는 별도 설정 (개발용으로 관대한 설정)
        CorsConfiguration swaggerConfig = new CorsConfiguration();
        swaggerConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        swaggerConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        swaggerConfig.setAllowedHeaders(Arrays.asList("*"));
        swaggerConfig.setAllowCredentials(true);
        swaggerConfig.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/swagger-ui/**", swaggerConfig);
        source.registerCorsConfiguration("/v3/api-docs/**", swaggerConfig);
        
        // 개발 환경에서 모든 경로에 대해 관대한 CORS 설정 (Swagger 테스트용)
        if ("dev".equals(activeProfile) || "local".equals(activeProfile)) {
            source.registerCorsConfiguration("/**", configuration);
        }

        return source;
    }
}
