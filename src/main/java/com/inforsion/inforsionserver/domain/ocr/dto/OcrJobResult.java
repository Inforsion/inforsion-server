package com.inforsion.inforsionserver.domain.ocr.dto;

import com.inforsion.inforsionserver.global.enums.OcrJobStatus;
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
public class OcrJobResult {
    
    private String jobId;
    
    private OcrJobStatus status;
    
    private String originalFileName;
    
    private String extractedText;
    
    private List<String> extractedLines;
    
    private List<ReceiptItem> receiptItems;
    
    private String ocrEngine;
    
    private Double confidence;
    
    private Long processingTimeMs;
    
    private Long fileSizeBytes;
    
    private String errorMessage;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private Integer progress; // 0-100
    
    public static OcrJobResult pending(String jobId, String originalFileName) {
        return OcrJobResult.builder()
                .jobId(jobId)
                .status(OcrJobStatus.PENDING)
                .originalFileName(originalFileName)
                .progress(0)
                .startedAt(LocalDateTime.now())
                .build();
    }
    
    public static OcrJobResult processing(String jobId, String originalFileName) {
        return OcrJobResult.builder()
                .jobId(jobId)
                .status(OcrJobStatus.PROCESSING)
                .originalFileName(originalFileName)
                .progress(50)
                .startedAt(LocalDateTime.now())
                .build();
    }
    
    public static OcrJobResult completed(String jobId, String originalFileName, 
                                       String extractedText, List<String> extractedLines, 
                                       List<ReceiptItem> receiptItems, String ocrEngine, 
                                       Double confidence, Long processingTimeMs, Long fileSizeBytes) {
        return OcrJobResult.builder()
                .jobId(jobId)
                .status(OcrJobStatus.COMPLETED)
                .originalFileName(originalFileName)
                .extractedText(extractedText)
                .extractedLines(extractedLines)
                .receiptItems(receiptItems)
                .ocrEngine(ocrEngine)
                .confidence(confidence)
                .processingTimeMs(processingTimeMs)
                .fileSizeBytes(fileSizeBytes)
                .progress(100)
                .completedAt(LocalDateTime.now())
                .build();
    }
    
    public static OcrJobResult failed(String jobId, String originalFileName, String errorMessage) {
        return OcrJobResult.builder()
                .jobId(jobId)
                .status(OcrJobStatus.FAILED)
                .originalFileName(originalFileName)
                .errorMessage(errorMessage)
                .progress(-1)
                .completedAt(LocalDateTime.now())
                .build();
    }
}