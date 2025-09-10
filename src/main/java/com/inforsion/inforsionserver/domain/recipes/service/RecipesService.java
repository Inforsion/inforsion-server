package com.inforsion.inforsionserver.domain.recipes.service;

import com.inforsion.inforsionserver.domain.recipes.Dto.request.RecipesRequestDto;
import com.inforsion.inforsionserver.domain.recipes.Dto.response.RecipesResponseDto;
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


@Service
@RequiredArgsConstructor
public class RecipesService {

    private final RecipesRepository recipesRepository;
    private final StoreRepository storeRepository;


    // 생성
    @Transactional
    public RecipesResponseDto createRecipes(@Valid RecipesRequestDto recipesRequestDto){
        StoreEntity store = storeRepository.findById(recipesRequestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        RecipesEntity recipesEntity = RecipesEntity.builder()
                .amountPerMenu(recipesRequestDto.getAmountPerMenu())
                .unit(recipesRequestDto.getUnit())
                .build();

        RecipesEntity saved = recipesRepository.save(recipesEntity);
        return RecipesResponseDto.fromEntity(saved);
    }

    // 조회
    @Transactional(readOnly = true)
    public Page<RecipesResponseDto> findRecipes(Integer storeId, Pageable pageable){
        Page<RecipesEntity> recipes = recipesRepository.findAllByStoreId(storeId, pageable);
        return recipes.map(RecipesResponseDto::fromEntity);
    }

    @Transactional
    public RecipesResponseDto updateRecipe(Integer recipesId, RecipesRequestDto recipesRequestDto){
        RecipesEntity recipes = recipesRepository.findById(recipesId)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다. recipesId = " + recipesId));
        recipes.setAmountPerMenu(recipesRequestDto.getAmountPerMenu());
        recipes.setUnit(recipesRequestDto.getUnit());

        RecipesEntity updated = recipesRepository.save(recipes);

        return RecipesResponseDto.fromEntity(updated);
    }

    @Transactional
    public void deleteRecipe(Integer recipesId){
        RecipesEntity entity = recipesRepository.findById(recipesId)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다. recipesId = " + recipesId));

        recipesRepository.delete(entity);
    }

}
