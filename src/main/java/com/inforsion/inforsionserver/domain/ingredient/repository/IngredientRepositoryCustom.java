package com.inforsion.inforsionserver.domain.ingredient.repository;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientSearchRequest;
import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;

import java.util.List;

public interface IngredientRepositoryCustom {
    
    List<IngredientEntity> searchIngredients(IngredientSearchRequest request);
}