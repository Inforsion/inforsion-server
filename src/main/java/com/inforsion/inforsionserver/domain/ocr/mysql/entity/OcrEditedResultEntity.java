package com.inforsion.inforsionserver.domain.ocr.mysql.entity;

import com.inforsion.inforsionserver.domain.ocr.dto.ReceiptItem;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ocr_edited_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OcrEditedResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mongo_ocr_result_id", nullable = false)
    private String mongoOcrResultId;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @ElementCollection
    @CollectionTable(name = "ocr_original_lines", joinColumns = @JoinColumn(name = "ocr_result_id"))
    @Column(name = "line_text")
    private List<String> originalLines;

    @Column(name = "edited_text", columnDefinition = "TEXT", nullable = false)
    private String editedText;

    @ElementCollection
    @CollectionTable(name = "ocr_edited_lines", joinColumns = @JoinColumn(name = "ocr_result_id"))
    @Column(name = "line_text")
    private List<String> editedLines;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "ocr_engine")
    private String ocrEngine;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
    
    @Column(name = "receipt_items", columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<ReceiptItem> receiptItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateEditedContent(String editedText, List<String> editedLines, String notes, List<ReceiptItem> receiptItems) {
        this.editedText = editedText;
        this.editedLines = editedLines;
        this.notes = notes;
        this.receiptItems = receiptItems;
    }
}