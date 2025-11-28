package com.inforsion.inforsionserver.domain.ingredient_dev.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredients_dev")
// Dedicated schema for the dev ingredient flow; intentionally separate from the legacy ingredients table.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class IngredientDevEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_dev_id")
    private Integer id;

    @Column(name = "ingredient_dev_name", nullable = false, length = 100)
    private String name;

    @Column(name = "dev_stock_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal stockPrice;

    @Column(name = "dev_unit_capacity", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitCapacity;

    @Column(name = "dev_stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "dev_image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void update(String name, BigDecimal stockPrice, BigDecimal unitCapacity, Integer stockQuantity) {
        if (name != null) {
            this.name = name;
        }
        if (stockPrice != null) {
            this.stockPrice = stockPrice;
        }
        if (unitCapacity != null) {
            this.unitCapacity = unitCapacity;
        }
        if (stockQuantity != null) {
            this.stockQuantity = stockQuantity;
        }
    }
}
