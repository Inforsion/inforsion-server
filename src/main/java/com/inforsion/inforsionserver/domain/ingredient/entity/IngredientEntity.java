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
        @UniqueConstraint(columnNames = {"name", "product_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class IngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "amount_per_product", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPerProduct;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "original_filename")
    private String originalFileName;

    @Column(name = "s3_key")
    private String s3Key;

    @Column(name = "is_active")
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

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryEntity> inventories;


    /**
     * 재료 정보 업데이트
     */
    public void update(String name, BigDecimal amountPerProduct, String unit, String description, String imageUrl) {
        if (name != null) this.name = name;
        if (amountPerProduct != null) this.amountPerProduct = amountPerProduct;
        if (unit != null) this.unit = unit;
        if (description != null) this.description = description;
        if (imageUrl != null) this.imageUrl = imageUrl;
    }

    /**
     * 이미지 URL만 업데이트
     */
    public void updateImageUrl(String imageUrl) {
        if (imageUrl != null) this.imageUrl = imageUrl;
    }

    /**
     * 이미지 보유 확인
     */
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.isBlank();
    }

    /**
     * 활성화 상태 업데이트
     */
    public void updateActiveStatus(Boolean isActive) {
        if (isActive != null) {
            this.isActive = isActive;
        }
    }

    /**
     * 이미지 메타데이터 업데이트
     */
    public void updateImageMetadata(String imageUrl, String originalFileName, String s3Key) {
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (originalFileName != null) this.originalFileName = originalFileName;
        if (s3Key != null) this.s3Key = s3Key;
    }
}