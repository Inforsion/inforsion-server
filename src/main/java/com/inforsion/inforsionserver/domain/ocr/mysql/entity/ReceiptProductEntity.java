package com.inforsion.inforsionserver.domain.ocr.mysql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "receipt_products", indexes = {
    @Index(name = "idx_product_name_normalized", columnList = "product_name_normalized"),
    @Index(name = "idx_ocr_job_id", columnList = "ocr_job_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptProductEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_name", nullable = false, length = 500)
    private String productName;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "unit_price")
    private Integer unitPrice;
    
    @Column(name = "total_price")
    private Integer totalPrice;
    
    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;
    
    @Column(name = "ocr_job_id", length = 100)
    private String ocrJobId;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "product_name_normalized", length = 500)
    private String productNameNormalized;
    
    @PrePersist
    private void prePersist() {
        if (productName != null) {
            productNameNormalized = productName.toLowerCase().replaceAll("\\s+", "");
        }
    }
    
    @PreUpdate
    private void preUpdate() {
        if (productName != null) {
            productNameNormalized = productName.toLowerCase().replaceAll("\\s+", "");
        }
    }
}