package com.inforsion.inforsionserver.domain.recipes.entity;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Builder
public class RecipesEntity {
    @Id
    @Column(name = "recipe_id")
    private Integer id;

    @Column(name = "recipe_name")
    private String name;

    @Column(name = "amount_per_menu")
    private BigDecimal amountPerMenu;

    @Column(name = "unit")
    private String unit; // 단위

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "create_at")
    private Timestamp createdAt;

    @Column(name = "update_at")
    private Timestamp updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;
}
