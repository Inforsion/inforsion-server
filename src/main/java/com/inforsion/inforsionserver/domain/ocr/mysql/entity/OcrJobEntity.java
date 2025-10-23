package com.inforsion.inforsionserver.domain.ocr.mysql.entity;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrJobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ocr_jobs")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OcrJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Integer id;

    @Column(name = "job_uuid", nullable = false, unique = true, length = 36)
    private String jobUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "file_size")
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OcrJobStatus status;

    @Column(name = "progress_percentage")
    @Builder.Default
    private Integer progressPercentage = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "raw_data_id")
    private Integer rawDataId;

    @Column(name = "matching_result_json", columnDefinition = "JSON")
    private String matchingResultJson;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateStatus(OcrJobStatus newStatus) {
        this.status = newStatus;
        if (newStatus == OcrJobStatus.PROCESSING) {
            this.startedAt = LocalDateTime.now();
            this.progressPercentage = 5;
        } else if (newStatus == OcrJobStatus.COMPLETED) {
            this.completedAt = LocalDateTime.now();
            this.progressPercentage = 100;
        } else if (newStatus == OcrJobStatus.FAILED) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public void updateProgress(Integer percentage) {
        this.progressPercentage = Math.max(0, Math.min(100, percentage));
    }

    public void setError(String errorMessage) {
        this.status = OcrJobStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }
}