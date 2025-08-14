package com.inforsion.inforsionserver.domain.product.entity;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 50)
    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder.Default
    @Column(name = "is_signature", nullable = false)
    private Boolean isSignature = false;

    @Builder.Default
    @Column(name = "in_stock", nullable = false)
    private Boolean inStock = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<IngredientEntity> ingredients;
}