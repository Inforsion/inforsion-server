package com.inforsion.inforsionserver.domain.inventory.service;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.repository.IngredientRepository;
import com.inforsion.inforsionserver.domain.inventory.dto.ExpiringInventoryDto;
import com.inforsion.inforsionserver.domain.inventory.dto.request.InventoryCreateRequest;
import com.inforsion.inforsionserver.domain.inventory.dto.request.InventoryUpdateRequest;
import com.inforsion.inforsionserver.domain.inventory.dto.response.InventoryResponse;
import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.inventory.repository.InventoryRepository;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.product.repository.ProductRepository;
import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
import com.inforsion.inforsionserver.domain.recipes.repository.RecipesRepository;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.global.error.code.ErrorCode;
import com.inforsion.inforsionserver.global.error.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final RecipesRepository recipesRepository;

    // 생성
    @Transactional
    public InventoryResponse createInventory(@Valid InventoryCreateRequest request) {
        StoreEntity store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND,
                        "매장을 찾을 수 없습니다. ID: " + request.getStoreId()));

        // Ingredient 찾거나 생성
        IngredientEntity ingredient;
        if (request.getIngredientId() != null) {
            // ingredientId가 있으면 기존 Ingredient 사용
            ingredient = ingredientRepository.findById(request.getIngredientId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.INGREDIENT_NOT_FOUND,
                            "재료를 찾을 수 없습니다. ID: " + request.getIngredientId()));
        } else {
            // ingredientId가 없으면 ingredientName으로 찾거나 생성
            if (request.getIngredientName() == null || request.getIngredientName().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "재료 ID 또는 재료명이 필요합니다.");
            }
            if (request.getUnit() == null || request.getUnit().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "단위는 필수입니다.");
            }

            // 같은 매장에서 같은 이름의 재료가 있는지 확인
            ingredient = ingredientRepository.findByStoreIdAndName(request.getStoreId(), request.getIngredientName())
                    .orElseGet(() -> {
                        // 없으면 새로 생성
                        IngredientEntity newIngredient = IngredientEntity.builder()
                                .store(store)
                                .name(request.getIngredientName())
                                .unit(request.getUnit())
                                .isActive(true)
                                .build();
                        return ingredientRepository.save(newIngredient);
                    });
        }

        // 입고일이 없으면 오늘로 설정
        LocalDate restockedDate = request.getLastRestockedDate() != null
                ? request.getLastRestockedDate()
                : LocalDate.now();

        InventoryEntity entity = InventoryEntity.builder()
                .ingredient(ingredient)
                .currentStock(request.getCurrentStock())
                .minStock(request.getMinStock())
                .maxStock(request.getMaxStock())
                .unitCost(request.getUnitCost())
                .expiryDate(request.getExpiryDate())
                .lastRestockedDate(restockedDate)
                .store(store)
                .build();

        InventoryEntity saved = inventoryRepository.save(entity);

        return InventoryResponse.from(saved);
    }

    // 조회
    @Transactional(readOnly = true)
    public Page<InventoryResponse> getInventories(Integer storeId, Pageable pageable){
        Page<InventoryEntity> entityPage = inventoryRepository.findInventories(storeId, pageable);
        return entityPage.map(InventoryResponse::from);
    }

    // 수정
    @Transactional
    public InventoryResponse updateInventory(Integer inventoryId, @Valid InventoryUpdateRequest request) {
        InventoryEntity entity = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND,
                        "재고 내역을 찾을 수 없습니다. ID: " + inventoryId));

        // 재료 변경
        if (request.getIngredientId() != null &&
            !request.getIngredientId().equals(entity.getIngredient().getId())) {
            IngredientEntity ingredient = ingredientRepository.findById(request.getIngredientId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.INGREDIENT_NOT_FOUND,
                            "재료를 찾을 수 없습니다. ID: " + request.getIngredientId()));
            entity.setIngredient(ingredient);
        }

        // 필드 업데이트
        if (request.getCurrentStock() != null) {
            entity.setCurrentStock(request.getCurrentStock());
        }
        if (request.getMinStock() != null) {
            entity.setMinStock(request.getMinStock());
        }
        if (request.getMaxStock() != null) {
            entity.setMaxStock(request.getMaxStock());
        }
        if (request.getUnitCost() != null) {
            entity.setUnitCost(request.getUnitCost());
        }
        if (request.getExpiryDate() != null) {
            entity.setExpiryDate(request.getExpiryDate());
        }
        if (request.getLastRestockedDate() != null) {
            entity.setLastRestockedDate(request.getLastRestockedDate());
        }

        return InventoryResponse.from(entity);
    }

    // 삭제
    @Transactional
    public void deleteInventory(Integer inventoryId) {
        InventoryEntity entity = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Id" + inventoryId + "에 해당하는 재고가 없습니다."));

        inventoryRepository.delete(entity);
    }

    // 유통기한 임박 알림
    @Transactional
    public List<ExpiringInventoryDto> getExpiringItems(Integer days){
        return inventoryRepository.findItemsExpiringBefore(days);
    }

    // 포함 메뉴 추가 (Ingredient와 Product 연결)
    @Transactional
    public void addProductsToIngredient(Integer inventoryId, List<Integer> productIds) {
        // Inventory 조회
        InventoryEntity inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND,
                        "재고를 찾을 수 없습니다. ID: " + inventoryId));

        IngredientEntity ingredient = inventory.getIngredient();
        StoreEntity store = inventory.getStore();

        // Product가 없어도 작동하도록 구현
        for (Integer productId : productIds) {
            // Product 조회 (없으면 건너뛰기)
            ProductEntity product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                // Product가 없으면 건너뛰기 (Product 데이터가 없어도 API 사용 가능)
                continue;
            }

            // 이미 Recipe가 존재하는지 확인
            boolean exists = recipesRepository.existsByStoreAndMenuAndIngredient(store, product, ingredient);
            if (!exists) {
                // Recipe 생성
                RecipesEntity recipe = RecipesEntity.builder()
                        .store(store)
                        .menu(product)
                        .ingredient(ingredient)
                        .amountPerMenu(BigDecimal.ZERO) // 기본값, 나중에 수정 가능
                        .unit(ingredient.getUnit()) // Ingredient의 단위 사용
                        .isActive(true)
                        .build();
                recipesRepository.save(recipe);
            }
        }
    }
}
