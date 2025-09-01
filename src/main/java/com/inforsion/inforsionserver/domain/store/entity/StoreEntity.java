package com.inforsion.inforsionserver.domain.store.entity;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    // TODO: 추후 필요시 주석 해제
    // @Column(name = "phone_number", length = 20)
    // private String phoneNumber;

    // @Column(name = "business_registration_number", unique = true, length = 20)
    // private String businessRegistrationNumber;

    // @Column(length = 100)
    // private String email;

    // @Column(name = "opening_hours", columnDefinition = "JSON")
    // private String openingHours;

    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;

    @Column(name = "original_filename")
    private String originalFileName;

    @Column(name = "s3_key")
    private String s3Key;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductEntity> products;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryEntity> inventories;

    public void update(String name, String location, String description, Boolean isActive) {
        if (name != null) this.name = name;
        if (location != null) this.location = location;
        if (description != null) this.description = description;
        // TODO: 추후 필요시 주석 해제
        // if (phoneNumber != null) this.phoneNumber = phoneNumber;
        // if (email != null) this.email = email;
        // if (businessRegistrationNumber != null) this.businessRegistrationNumber = businessRegistrationNumber;
        // if (openingHours != null) this.openingHours = openingHours;
        if (isActive != null) this.isActive = isActive;
    }

    /**
     * 썸네일 이미지 메타데이터 업데이트
     */
    public void updateThumbnailMetadata(String thumbnailUrl, String originalFileName, String s3Key) {
        if (thumbnailUrl != null) this.thumbnailUrl = thumbnailUrl;
        if (originalFileName != null) this.originalFileName = originalFileName;
        if (s3Key != null) this.s3Key = s3Key;
    }

    /**
     * 썸네일 이미지 보유 확인
     */
    public boolean hasThumbnail() {
        return thumbnailUrl != null && !thumbnailUrl.isBlank();
    }
}