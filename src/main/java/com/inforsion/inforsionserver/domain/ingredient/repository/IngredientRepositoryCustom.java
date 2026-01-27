package com.inforsion.inforsionserver.domain.ingredient.repository;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;

import java.util.Optional;

public interface IngredientRepositoryCustom {

    Optional<IngredientEntity> findByStoreIdAndNameAndIsActive(Integer storeId, String name, Boolean isActive);
}