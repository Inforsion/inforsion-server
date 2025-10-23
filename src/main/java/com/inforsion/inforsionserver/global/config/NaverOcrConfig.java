package com.inforsion.inforsionserver.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "naver.ocr")
@Getter
@Setter
public class NaverOcrConfig {
    private String apiUrl;
    private String secretKey;
}