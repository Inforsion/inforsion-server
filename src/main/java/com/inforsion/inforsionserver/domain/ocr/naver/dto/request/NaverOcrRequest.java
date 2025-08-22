package com.inforsion.inforsionserver.domain.ocr.naver.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NaverOcrRequest {
    private String version;
    private String requestId;
    private Long timestamp;
    private String lang;
    private List<Image> images;
    
    @Getter
    @Builder
    public static class Image {
        private String format;
        private String name;
        private String data; // base64 encoded image
    }
}