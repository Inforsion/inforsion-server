package com.inforsion.inforsionserver.domain.recipe.repository;

import com.inforsion.inforsionserver.domain.recipe.entity.RecipeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeRepositoryCustom {
    
    Page<RecipeEntity> findRecipesByStoreIdWithPaging(Integer storeId, Pageable pageable);
    
    Page<RecipeEntity> findActiveRecipesByStoreIdWithPaging(Integer storeId, Pageable pageable);
    
    List<RecipeEntity> findRecipesByMenuIdWithIngredientDetails(Integer menuId);
    
    List<RecipeEntity> findRecipesUsingInventoryId(Integer inventoryId);
    
    List<RecipeEntity> findRecipesByStoreAndIngredientName(Integer storeId, String ingredientName);
    
    void deactivateRecipesByMenuId(Integer menuId);
    
    void deactivateRecipesByInventoryId(Integer inventoryId);
}