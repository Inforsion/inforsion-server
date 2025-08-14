package com.inforsion.inforsionserver.domain.ingredient.entity;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "ingredients",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryEntity> inventories;
}