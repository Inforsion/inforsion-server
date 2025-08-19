package com.inforsion.inforsionserver.domain.ingredient.service;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientCreateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientSearchRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientUpdateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.response.IngredientResponse;
import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.repository.IngredientRepository;
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

    /**
     * 새로운 재료를 생성합니다.
     * 
     * 상품 ID를 기반으로 유효한 상품인지 확인하고, 동일한 상품에 같은 이름의 재료가 있는지 검증합니다.
     * 재료명과 상품 ID 조합은 유니크해야 하므로 중복 시 예외를 발생시킵니다.
     * 
     * @param request 재료 생성 요청 DTO (재료명, 사용량, 단위, 설명, 상품 ID 포함)
     * @return 생성된 재료 정보 DTO
     * @throws ProductNotFoundException 상품이 존재하지 않는 경우
     * @throws DuplicateIngredientException 동일한 상품에 같은 이름의 재료가 이미 존재하는 경우
     */
    @Transactional
    public IngredientResponse createIngredient(IngredientCreateRequest request) {
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(ProductNotFoundException::new);

        if (ingredientRepository.existsByNameAndProductId(request.getName(), request.getProductId())) {
            throw new DuplicateIngredientException();
        }

        IngredientEntity ingredient = IngredientEntity.builder()
                .name(request.getName())
                .amountPerProduct(request.getAmountPerProduct())
                .unit(request.getUnit())
                .description(request.getDescription())
                .product(product)
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
    public IngredientResponse getIngredient(Integer ingredientId) {
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(IngredientNotFoundException::new);
        return IngredientResponse.from(ingredient);
    }

    /**
     * 시스템에 등록된 모든 재료 목록을 조회합니다.
     * 
     * @return 모든 재료 목록
     */
    public List<IngredientResponse> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(IngredientResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 상품에 사용되는 모든 재료를 조회합니다.
     * 
     * 메뉴 구성 확인, 레시피 관리, 원가 계산 등에 활용됩니다.
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
     * 특정 가게에서 사용하는 모든 재료를 조회합니다.
     * 
     * 가게의 모든 상품에 사용되는 재료를 중복 제거하여 반환합니다.
     * 가게 재료 관리, 통합 발주 관리에 활용됩니다.
     * 
     * @param storeId 가게 ID
     * @return 해당 가게에서 사용하는 모든 재료 목록 (중복 제거)
     */
    public List<IngredientResponse> getIngredientsByStore(Integer storeId) {
        return ingredientRepository.findIngredientsByStoreId(storeId).stream()
                .map(IngredientResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 동적 조건을 사용하여 재료를 검색합니다.
     * 
     * SearchRequest의 필드 중 null이 아닌 값들을 조건으로 사용하여 유연한 검색을 제공합니다.
     * 재료명 부분 검색, 상품별/가게별 필터링, 단위별/활성화 상태별 필터링이 가능합니다.
     * 
     * @param request 검색 조건 DTO (재료명, 상품ID, 가게ID, 단위, 활성화 상태)
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
     * 재료명 변경 시 동일한 상품에 같은 이름의 재료가 있는지 검증합니다.
     * null이 아닌 필드만 업데이트되며, 이미지는 별도 메서드로 처리합니다.
     * 
     * @param ingredientId 수정할 재료의 ID
     * @param request 수정할 정보 DTO (재료명, 사용량, 단위, 설명, 활성화 상태)
     * @return 수정된 재료 정보 DTO
     * @throws IngredientNotFoundException 재료가 존재하지 않는 경우
     * @throws DuplicateIngredientException 수정하려는 재료명이 동일한 상품에 이미 존재하는 경우
     */
    @Transactional
    public IngredientResponse updateIngredient(Integer ingredientId, IngredientUpdateRequest request) {
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(IngredientNotFoundException::new);

        if (request.getName() != null && !request.getName().equals(ingredient.getName())) {
            if (ingredientRepository.existsByNameAndProductId(request.getName(), ingredient.getProduct().getId())) {
                throw new DuplicateIngredientException();
            }
        }

        ingredient.update(
                request.getName(),
                request.getAmountPerProduct(),
                request.getUnit(),
                request.getDescription(),
                null // imageUrl은 별도 메서드로 처리
        );

        if (request.getIsActive() != null) {
            ingredient.updateActiveStatus(request.getIsActive());
        }

        return IngredientResponse.from(ingredient);
    }

    /**
     * 재료를 삭제합니다.
     * 
     * 삭제 전 재고(Inventory) 존재 여부를 확인하여 데이터 무결성을 보장합니다.
     * 재고가 존재하는 재료는 삭제할 수 없으며, 먼저 재고를 정리해야 합니다.
     * 
     * @param ingredientId 삭제할 재료의 ID
     * @throws IngredientNotFoundException 재료가 존재하지 않는 경우
     * @throws IllegalStateException 재고가 존재하여 삭제할 수 없는 경우
     */
    @Transactional
    public void deleteIngredient(Integer ingredientId) {
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(IngredientNotFoundException::new);

        // 재고가 존재하는 경우 삭제 방지
        if (ingredient.getInventories() != null && !ingredient.getInventories().isEmpty()) {
            throw new IllegalStateException("재고가 존재하는 재료는 삭제할 수 없습니다. 먼저 재고를 정리해주세요.");
        }

        ingredientRepository.delete(ingredient);
    }

    /**
     * 여러 상품에 공통으로 사용되는 재료를 조회합니다.
     * 
     * 주어진 모든 상품에서 공통으로 사용되는 재료만 반환합니다.
     * 메뉴 조합 추천, 공통 재료 관리, 대량 발주 계획에 활용됩니다.
     * 
     * @param productIds 상품 ID 목록
     * @return 모든 상품에 공통으로 사용되는 재료 목록
     */
    public List<IngredientResponse> getCommonIngredients(List<Integer> productIds) {
        return ingredientRepository.findCommonIngredients(productIds).stream()
                .map(IngredientResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 재고가 부족한 재료 목록을 조회합니다.
     * 
     * 특정 가게에서 재고량이 임계값 이하인 재료들을 조회합니다.
     * 재고 알림 시스템, 자동 발주 시스템, 재고 관리 대시보드에 활용됩니다.
     * 
     * @param storeId 가게 ID
     * @param threshold 재고 부족 임계값
     * @return 재고가 부족한 재료 목록
     */
    public List<IngredientResponse> getLowStockIngredients(Integer storeId, Double threshold) {
        return ingredientRepository.findIngredientsWithLowStock(storeId, threshold).stream()
                .map(IngredientResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 상품에 사용되는 재료의 개수를 조회합니다.
     * 
     * 상품의 복잡도 분석, 원가 계산의 복잡도 측정, 레시피 관리에 활용됩니다.
     * 
     * @param productId 상품 ID
     * @return 해당 상품에 사용되는 재료 개수
     * @throws ProductNotFoundException 상품이 존재하지 않는 경우
     */
    public Long countIngredientsByProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException();
        }
        return ingredientRepository.countIngredientsByProductId(productId);
    }

    /**
     * 재료의 이미지 정보를 업데이트합니다.
     * 
     * S3 업로드 후 받은 이미지 URL과 메타데이터를 재료에 저장합니다.
     * 기존 이미지가 있는 경우 새 이미지로 교체됩니다.
     * 
     * @param ingredientId 재료 ID
     * @param imageUrl S3 이미지 URL
     * @param originalFileName 원본 파일명 (선택사항)
     * @param s3Key S3 객체 키 (선택사항)
     * @return 업데이트된 재료 정보 DTO
     * @throws IngredientNotFoundException 재료가 존재하지 않는 경우
     */
    @Transactional
    public IngredientResponse updateIngredientImage(Integer ingredientId, String imageUrl, String originalFileName, String s3Key) {
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(IngredientNotFoundException::new);

        ingredient.updateImageMetadata(imageUrl, originalFileName, s3Key);

        return IngredientResponse.from(ingredient);
    }

    /**
     * 재료의 이미지를 삭제합니다.
     * 
     * 재료 엔티티에서 이미지 관련 정보를 제거합니다.
     * 실제 S3 파일 삭제는 별도의 파일 관리 서비스에서 처리해야 합니다.
     * 
     * @param ingredientId 재료 ID
     * @throws IngredientNotFoundException 재료가 존재하지 않는 경우
     * 
     * @apiNote 향후 S3 파일 삭제 로직이 추가되어야 합니다.
     */
    @Transactional
    public void deleteIngredientImage(Integer ingredientId) {
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(IngredientNotFoundException::new);

        ingredient.updateImageMetadata(null, null, null);
    }
}
