package com.inforsion.inforsionserver.domain.inventory.entity;

import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrResultEntity;
import com.inforsion.inforsionserver.domain.order.entity.OrderEntity;
import com.inforsion.inforsionserver.global.enums.InventoryLogType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_logs", indexes = {
        @Index(name = "idx_inventory_logs_created_at", columnList = "created_at"),
        @Index(name = "idx_inventory_logs_log_type", columnList = "log_type"),
        @Index(name = "idx_inventory_logs_inventory_id", columnList = "inventory_id"),
        @Index(name = "idx_inventory_logs_order_id", columnList = "order_id"),
        @Index(name = "idx_inventory_logs_ocr_id", columnList = "ocr_id")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InventoryLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryEntity inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ocr_id")
    private OcrResultEntity ocrResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false)
    private InventoryLogType logType;

    @Column(name = "quantity_change", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityChange;

    @Column(name = "before_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal beforeQuantity;

    @Column(name = "after_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal afterQuantity;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}