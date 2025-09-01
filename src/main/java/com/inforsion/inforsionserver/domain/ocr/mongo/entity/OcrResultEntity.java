package com.inforsion.inforsionserver.domain.ocr.mongo.entity;

import com.inforsion.inforsionserver.domain.ocr.dto.ReceiptItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "ocr_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResultEntity {

    @Id
    private String id;

    private String originalFileName;
    
    private String extractedText;
    
    private List<String> extractedLines;
    
    private long processingTimeMs;
    
    private long fileSizeBytes;
    
    private String ocrEngine;
    
    private double confidence;
    
    private List<ReceiptItem> receiptItems;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}