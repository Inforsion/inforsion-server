package com.inforsion.inforsionserver.domain.ingredient.service;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientCreateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientSearchRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientUpdateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.response.IngredientResponse;
import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.repository.IngredientRepository;
import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.inventory.repository.InventoryRepository;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.product.repository.ProductRepository;
import com.inforsion.inforsionserver.global.error.exception.IngredientNotFoundException;
import com.inforsion.inforsionserver.global.error.exception.ProductNotFoundException;
import com.inforsion.inforsionserver.global.error.exception.DuplicateIngredientException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * 새로운 재료를 생성합니다.
     * 
     * 상품 ID와 재고 ID를 기반으로 유효한지 확인하고, 동일한 상품에 같은 재고가 이미 사용되고 있는지 검증합니다.
     * 상품 ID와 재고 ID 조합은 유니크해야 하므로 중복 시 예외를 발생시킵니다.
     * 
     * @param request 재료 생성 요청 DTO (재고 ID, 사용량, 단위, 설명, 상품 ID 포함)
     * @return 생성된 재료 정보 DTO
     * @throws ProductNotFoundException 상품이 존재하지 않는 경우
     * @throws RuntimeException 재고가 존재하지 않는 경우
     * @throws DuplicateIngredientException 동일한 상품에 같은 재고가 이미 사용되는 경우
     */
    @Transactional
    public IngredientResponse createIngredient(IngredientCreateRequest request) {
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(ProductNotFoundException::new);

        InventoryEntity inventory = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new RuntimeException("재고를 찾을 수 없습니다"));

        if (ingredientRepository.existsByProductIdAndInventoryId(request.getProductId(), request.getInventoryId())) {
            throw new DuplicateIngredientException();
        }

        IngredientEntity ingredient = IngredientEntity.builder()
                .amountPerProduct(request.getAmountPerProduct())
                .unit(request.getUnit())
                .description(request.getDescription())
                .product(product)
                .inventory(inventory)
                .build();

        IngredientEntity savedIngredient = ingredientRepository.save(ingredient);
        return IngredientResponse.from(savedIngredient);
    }

    /**
     * 특정 ID의 재료 상세 정보를 조회합니다.
     * 
     * @param ingredientId 조회할 재료의 ID
     * @return 재료 상세 정보 DTO
     * @throws IngredientNotFoundException 재료가 존재하지 않는 경우
     */
    @Transactional
    public IngredientResponse getIngredient(Integer ingredientId) {
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(IngredientNotFoundException::new);
        return IngredientResponse.from(ingredient);
    }

    /**
     * 특정 상품에 사용되는 모든 재료를 조회합니다.
     * 
     * 메뉴 구성 확인, 레시피 관리에 활용됩니다.
     * 
     * @param productId 상품 ID
     * @return 해당 상품에 사용되는 재료 목록
     * @throws ProductNotFoundException 상품이 존재하지 않는 경우
     */
    public List<IngredientResponse> getIngredientsByProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException();
        }

        return ingredientRepository.findByProductId(productId).stream()
                .map(IngredientResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 동적 조건을 사용하여 재료를 검색합니다.
     * 
     * SearchRequest의 필드 중 null이 아닌 값들을 조건으로 사용하여 유연한 검색을 제공합니다.
     * 재료명 부분 검색, 상품별 필터링, 단위별/활성화 상태별 필터링이 가능합니다.
     * 
     * @param request 검색 조건 DTO (재료명, 상품ID, 단위, 활성화 상태)
     * @return 검색 조건에 맞는 재료 목록 (생성일시 역순)
     */
    public List<IngredientResponse> searchIngredients(IngredientSearchRequest request) {
        return ingredientRepository.searchIngredients(request).stream()
                .map(IngredientResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 기존 재료의 정보를 수정합니다.
     * 
     * 재고 ID 변경 시 동일한 상품에 같은 재고가 이미 사용되고 있는지 검증합니다.
     * null이 아닌 필드만 업데이트되며, 이미지는 별도 메서드로 처리합니다.
     * 
     * @param ingredientId 수정할 재료의 ID
     * @param request 수정할 정보 DTO (재고 ID, 사용량, 단위, 설명, 활성화 상태)
     * @return 수정된 재료 정보 DTO
     * @throws IngredientNotFoundException 재료가 존재하지 않는 경우
     * @throws DuplicateIngredientException 수정하려는 재고가 동일한 상품에 이미 사용되는 경우
     */
    @Transactional
    public IngredientResponse updateIngredient(Integer ingredientId, IngredientUpdateRequest request) {
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(IngredientNotFoundException::new);

        // 재고 ID 변경 시 중복 체크
        if (request.getInventoryId() != null && 
            !request.getInventoryId().equals(ingredient.getInventory().getId())) {
            
            if (ingredientRepository.existsByProductIdAndInventoryId(
                ingredient.getProduct().getId(), request.getInventoryId())) {
                throw new DuplicateIngredientException();
            }
            
            // 새로운 재고 엔티티 조회
            InventoryEntity newInventory = inventoryRepository.findById(request.getInventoryId())
                    .orElseThrow(() -> new RuntimeException("재고를 찾을 수 없습니다"));
        }

        ingredient.update(
                request.getAmountPerProduct(),
                request.getUnit(),
                request.getDescription()
        );

        if (request.getIsActive() != null) {
            ingredient.updateActiveStatus(request.getIsActive());
        }

        return IngredientResponse.from(ingredient);
    }

    /**
     * 재료를 삭제합니다.
     * 
     * @param ingredientId 삭제할 재료의 ID
     * @throws IngredientNotFoundException 재료가 존재하지 않는 경우
     */
    @Transactional
    public void deleteIngredient(Integer ingredientId) {
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(IngredientNotFoundException::new);

        ingredientRepository.delete(ingredient);
    }

}