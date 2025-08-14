package com.inforsion.inforsionserver.domain.ingredient.repository;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;

import java.util.List;

public interface IngredientRepositoryCustom {
    
    List<IngredientEntity> findIngredientsByStoreId(Integer storeId);
    
    List<IngredientEntity> findCommonIngredients(List<Integer> productIds);
    
    List<IngredientEntity> findIngredientsWithLowStock(Integer storeId, Double threshold);
    
    Long countIngredientsByProductId(Integer productId);
}