package com.inforsion.inforsionserver.domain.ingredient.entity;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 재료 마스터 엔티티
 * 재료의 종류를 정의 (예: 우유, 원두, 시럽)
 */
@Entity
@Table(name = "ingredients",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"store_id", "name"})
    })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class IngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // 재료명 (예: "우유", "원두")

    @Column(name = "unit", nullable = false, length = 20)
    private String unit; // 단위 (g, ml, 개 등)

    @Column(name = "default_expiry_days")
    private Integer defaultExpiryDays; // 기본 유통기한 (일)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 재료 설명

    @Column(name = "image_url")
    private String imageUrl; // 이미지 URL

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 활성 상태

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store; // 매장

    /**
     * 재료 정보 업데이트
     */
    public void update(String name, String unit, Integer defaultExpiryDays, String description) {
        if (name != null) this.name = name;
        if (unit != null) this.unit = unit;
        if (defaultExpiryDays != null) this.defaultExpiryDays = defaultExpiryDays;
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

    /**
     * 이미지 URL 업데이트
     */
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}