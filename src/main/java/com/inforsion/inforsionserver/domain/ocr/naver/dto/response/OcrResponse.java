package com.inforsion.inforsionserver.domain.ocr.naver.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OcrResponse {
    private String originalFileName;
    private String extractedText;
    private List<String> extractedLines;
    private Long processingTimeMs;
    private Long fileSizeBytes;
    private Integer imageWidth;
    private Integer imageHeight;
    private String ocrEngine;
    private Double confidence;
}