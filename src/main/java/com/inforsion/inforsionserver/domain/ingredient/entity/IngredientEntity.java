package com.inforsion.inforsionserver.domain.ingredient.entity;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ingredients",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "inventory_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class IngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "amount_per_product", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPerProduct;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(columnDefinition = "TEXT")
    private String description;


    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private InventoryEntity inventory;


    /**
     * 재료 정보 업데이트
     */
    public void update(BigDecimal amountPerProduct, String unit, String description) {
        if (amountPerProduct != null) this.amountPerProduct = amountPerProduct;
        if (unit != null) this.unit = unit;
        if (description != null) this.description = description;
    }

    /**
     * 활성화 상태 업데이트
     */
    public void updateActiveStatus(Boolean isActive) {
        if (isActive != null) {
            this.isActive = isActive;
        }
    }

}