package com.inforsion.inforsionserver.domain.inventory.entity;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.global.enums.StockStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InventoryEntity {

    @Id
    @Column(name = "ingredient_id")
    private Integer id;

    @Column(name = "ingredient_name", nullable = false, length = 100)
    private String name; // 재료명

    @Column(nullable = false, precision = 10)
    private BigDecimal currentStock; // 현재 재료량

    @Column(name = "min_stock_level")
    private BigDecimal minStock; // 최대 재고 수준

    @Column(name = "max_stock_level")
    private BigDecimal maxStock; // 최소 재고 수준

    @Column(nullable = false, length = 20)
    private String unit; // 단위 (g, ml, 개 등)

    @Column(name = "unit_cost", nullable = false, length = 20)
    private BigDecimal unitCost; // 단위 당 가격

    @Column(name = "last_restocked_date")
    private LocalDate lastRestockedDate; // 마지막 입고일

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate; // 유통기한

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "stock_status", nullable = false)
    private StockStatus stockStatus = StockStatus.SUFFICIENT; // 재고 상태

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;
}