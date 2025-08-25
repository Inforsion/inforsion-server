package com.inforsion.inforsionserver.domain.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrEditResponse {

    private String mongoOcrResultId;

    private String originalFileName;

    private String originalText;

    private List<String> originalLines;

    private String editedText;

    private List<String> editedLines;

    private String notes;
    
    private List<ReceiptItem> receiptItems;

    private String ocrEngine;

    private double confidence;

    private long processingTimeMs;

    private long fileSizeBytes;

    private Integer userId;

    private String userName;

    private LocalDateTime createdAt;

    private LocalDateTime editedAt;
}