package com.inforsion.inforsionserver.domain.ocr.naver.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NaverOcrResponse {
    private String version;
    private String requestId;
    private Long timestamp;
    private List<Image> images;
    
    @Getter
    @NoArgsConstructor
    public static class Image {
        private String uid;
        private String name;
        private String inferResult;
        private String message;
        private List<Field> fields;
        private ValidationResult validationResult;
    }
    
    @Getter
    @NoArgsConstructor
    public static class Field {
        private String valueType;
        private BoundingPoly boundingPoly;
        private String inferText;
        private Double inferConfidence;
        private String type;
        private String lineBreak;
    }
    
    @Getter
    @NoArgsConstructor
    public static class BoundingPoly {
        private List<Vertex> vertices;
    }
    
    @Getter
    @NoArgsConstructor
    public static class Vertex {
        private Double x;
        private Double y;
    }
    
    @Getter
    @NoArgsConstructor
    public static class ValidationResult {
        private String result;
    }
}