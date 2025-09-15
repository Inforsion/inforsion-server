package com.inforsion.inforsionserver.domain.recipes.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@Table(name = "recipe_categories")
public class RecipesCategory {
    @Id
    @Column(name = "recipe_categories_id")
    private Integer Id;

    @Column(name = "recipe_categories_name")
    private String name;

    private String description;
}
