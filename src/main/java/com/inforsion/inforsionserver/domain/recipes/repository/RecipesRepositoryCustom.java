package com.inforsion.inforsionserver.domain.recipes.repository;

import com.inforsion.inforsionserver.domain.recipes.Dto.request.RecipesRequestDto;
import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipesRepositoryCustom {
    Page<RecipesEntity> findRecipes(Integer storeId, Pageable pageable);
    Long updateRecipe(Integer recipesId, RecipesRequestDto recipesRequestDto);
    Long deleteRecipe(Integer recipesId);
}
