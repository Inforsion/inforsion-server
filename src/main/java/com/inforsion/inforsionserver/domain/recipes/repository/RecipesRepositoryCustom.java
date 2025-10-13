package com.inforsion.inforsionserver.domain.recipes.repository;

import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipesRepositoryCustom {

    Page<RecipesEntity> findRecipesByStoreIdWithPaging(Integer storeId, Pageable pageable);

    Page<RecipesEntity> findActiveRecipesByStoreIdWithPaging(Integer storeId, Pageable pageable);

    List<RecipesEntity> findRecipesByMenuIdWithIngredientDetails(Integer menuId);

    List<RecipesEntity> findRecipesUsingInventoryId(Integer inventoryId);

    List<RecipesEntity> findRecipesByStoreAndIngredientName(Integer storeId, String ingredientName);

    void deactivateRecipesByMenuId(Integer menuId);

    void deactivateRecipesByInventoryId(Integer inventoryId);
}
