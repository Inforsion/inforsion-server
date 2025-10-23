package com.inforsion.inforsionserver.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kakao.local")
@Getter
@Setter
public class KakaoMapConfig {

    /**
     * 카카오 주소 검색 API 엔드포인트 (기본값: https://dapi.kakao.com/v2/local/search/address.json)
     */
    private String addressSearchUrl;

    /**
     * 카카오 REST API 키 (KakaoAK {REST_API_KEY})
     */
    private String restApiKey;

    /**
     * 주소 검색 시 기본 페이지 사이즈
     */
    private int defaultSize = 10;
}
