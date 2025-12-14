package com.inforsion.inforsionserver.domain.recipes.service;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.repository.IngredientRepository;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.product.repository.ProductRepository;
import com.inforsion.inforsionserver.domain.recipes.dto.request.RecipesRequestDto;
import com.inforsion.inforsionserver.domain.recipes.dto.response.RecipesResponseDto;
import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
import com.inforsion.inforsionserver.domain.recipes.repository.RecipesRepository;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipesService {

    private final RecipesRepository recipesRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public RecipesResponseDto createRecipes(@Valid RecipesRequestDto request) {
        StoreEntity store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다. storeId=" + request.getStoreId()));
        ProductEntity menu = productRepository.findById(request.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다. menuId=" + request.getMenuId()));
        IngredientEntity ingredient = ingredientRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new IllegalArgumentException("재료를 찾을 수 없습니다. ingredientId=" + request.getInventoryId()));

        Boolean isActive = Optional.ofNullable(request.getIsActive()).orElse(true);

        RecipesEntity entity = RecipesEntity.builder()
                .store(store)
                .menu(menu)
                .ingredient(ingredient)
                .name(request.getName())
                .amountPerMenu(request.getAmountPerMenu())
                .unit(request.getUnit())
                .isActive(isActive)
                .build();

        RecipesEntity saved = recipesRepository.save(entity);
        return RecipesResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<RecipesResponseDto> findRecipes(Integer storeId, Pageable pageable) {
        Page<RecipesEntity> recipes = recipesRepository.findAllByStoreId(storeId, pageable);
        return recipes.map(RecipesResponseDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public RecipesEntity getRecipeDetail(Integer recipeId) {
        return recipesRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 레시피가 존재하지 않습니다. recipeId=" + recipeId));
    }

    @Transactional
    public RecipesResponseDto updateRecipe(Integer recipesId, RecipesRequestDto request) {
        RecipesEntity recipes = recipesRepository.findById(recipesId)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다. recipesId=" + recipesId));

        if (request.getName() != null) {
            recipes.setName(request.getName());
        }
        if (request.getAmountPerMenu() != null) {
            recipes.setAmountPerMenu(request.getAmountPerMenu());
        }
        if (request.getUnit() != null) {
            recipes.setUnit(request.getUnit());
        }
        if (request.getIsActive() != null) {
            if (Boolean.TRUE.equals(request.getIsActive())) {
                recipes.activate();
            } else {
                recipes.deactivate();
            }
        }

        if (request.getMenuId() != null && (recipes.getMenu() == null || !request.getMenuId().equals(recipes.getMenu().getId()))) {
            ProductEntity menu = productRepository.findById(request.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다. menuId=" + request.getMenuId()));
            recipes.setMenu(menu);
        }

        if (request.getInventoryId() != null && (recipes.getIngredient() == null || !request.getInventoryId().equals(recipes.getIngredient().getId()))) {
            IngredientEntity ingredient = ingredientRepository.findById(request.getInventoryId())
                    .orElseThrow(() -> new IllegalArgumentException("재료를 찾을 수 없습니다. ingredientId=" + request.getInventoryId()));
            recipes.setIngredient(ingredient);
        }

        RecipesEntity updated = recipesRepository.save(recipes);
        return RecipesResponseDto.fromEntity(updated);
    }

    @Transactional
    public void deleteRecipe(Integer recipesId) {
        RecipesEntity entity = recipesRepository.findById(recipesId)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다. recipesId=" + recipesId));

        recipesRepository.delete(entity);
    }
}
