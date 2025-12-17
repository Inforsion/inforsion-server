package com.inforsion.inforsionserver.domain.ingredient.service;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientCreateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientUpdateRequest;
import com.inforsion.inforsionserver.domain.ingredient.dto.response.IngredientResponse;
import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.repository.IngredientRepository;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.global.error.code.ErrorCode;
import com.inforsion.inforsionserver.global.error.exception.BusinessException;
import com.inforsion.inforsionserver.global.service.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final StoreRepository storeRepository;
    private final S3FileUploadService s3FileUploadService;

    /**
     * 재료 생성
     */
    @Transactional
    public IngredientResponse createIngredient(IngredientCreateRequest request) {
        StoreEntity store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND,
                        "매장을 찾을 수 없습니다. ID: " + request.getStoreId()));

        // 같은 매장에 같은 이름의 재료가 이미 존재하는지 확인
        if (ingredientRepository.findByStoreIdAndName(request.getStoreId(), request.getName()).isPresent()) {
            throw new BusinessException(ErrorCode.INGREDIENT_ALREADY_EXISTS,
                    "이미 존재하는 재료명입니다: " + request.getName());
        }

        IngredientEntity ingredient = IngredientEntity.builder()
                .store(store)
                .name(request.getName())
                .unit(request.getUnit())
                .stockPrice(request.getStockPrice())
                .unitCapacity(request.getUnitCapacity())
                .stockQuantity(request.getStockQuantity())
                .isActive(true)
                .build();

        IngredientEntity saved = ingredientRepository.save(ingredient);
        return IngredientResponse.from(saved);
    }

    /**
     * 재료 단건 조회
     */
    public IngredientResponse getIngredient(Integer ingredientId) {
        IngredientEntity ingredient = getIngredientOrThrow(ingredientId);
        return IngredientResponse.from(ingredient);
    }

    /**
     * 매장별 재료 목록 조회 (페이징)
     */
    public Page<IngredientResponse> getIngredientsByStore(Integer storeId, Pageable pageable) {
        Page<IngredientEntity> ingredients = ingredientRepository.findByStoreIdAndIsActive(storeId, true)
                .stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
        return ingredients.map(IngredientResponse::from);
    }

    /**
     * 재료 수정
     */
    @Transactional
    public IngredientResponse updateIngredient(Integer ingredientId, IngredientUpdateRequest request) {
        IngredientEntity ingredient = getIngredientOrThrow(ingredientId);

        // 재료명이 변경되는 경우 중복 체크
        if (request.getName() != null && !request.getName().equals(ingredient.getName())) {
            if (ingredientRepository.findByStoreIdAndName(ingredient.getStore().getId(), request.getName()).isPresent()) {
                throw new BusinessException(ErrorCode.INGREDIENT_ALREADY_EXISTS,
                        "이미 존재하는 재료명입니다: " + request.getName());
            }
        }

        ingredient.update(
                request.getName(),
                null, // unit
                request.getStockPrice(),
                request.getUnitCapacity(),
                request.getStockQuantity(),
                null, // defaultExpiryDays
                null  // description
        );

        return IngredientResponse.from(ingredient);
    }

    /**
     * 재료 삭제 (소프트 삭제 - isActive = false)
     */
    @Transactional
    public void deleteIngredient(Integer ingredientId) {
        IngredientEntity ingredient = getIngredientOrThrow(ingredientId);
        ingredient.updateActiveStatus(false);
    }

    /**
     * 이미지 업로드
     */
    @Transactional
    public IngredientResponse uploadImage(Integer ingredientId, MultipartFile imageFile) {
        IngredientEntity ingredient = getIngredientOrThrow(ingredientId);
        String imageUrl = s3FileUploadService.uploadImageFile(imageFile, "ingredients");
        ingredient.updateImageUrl(imageUrl);
        return IngredientResponse.from(ingredient);
    }

    /**
     * 이미지 수정 (기존 이미지 삭제 후 새 이미지 업로드)
     */
    @Transactional
    public IngredientResponse updateImage(Integer ingredientId, MultipartFile imageFile) {
        IngredientEntity ingredient = getIngredientOrThrow(ingredientId);

        // 기존 이미지가 있으면 삭제
        if (ingredient.getImageUrl() != null) {
            s3FileUploadService.deleteFile(ingredient.getImageUrl());
        }

        String imageUrl = s3FileUploadService.uploadImageFile(imageFile, "ingredients");
        ingredient.updateImageUrl(imageUrl);
        return IngredientResponse.from(ingredient);
    }

    /**
     * 이미지 조회
     */
    public IngredientResponse getImage(Integer ingredientId) {
        IngredientEntity ingredient = getIngredientOrThrow(ingredientId);

        if (ingredient.getImageUrl() == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND,
                    "해당 재료에 등록된 이미지가 없습니다.");
        }

        return IngredientResponse.from(ingredient);
    }

    /**
     * 이미지 삭제
     */
    @Transactional
    public void deleteImage(Integer ingredientId) {
        IngredientEntity ingredient = getIngredientOrThrow(ingredientId);

        if (ingredient.getImageUrl() != null) {
            s3FileUploadService.deleteFile(ingredient.getImageUrl());
            ingredient.updateImageUrl(null);
        }
    }

    /**
     * 재료 조회 또는 예외 발생
     */
    private IngredientEntity getIngredientOrThrow(Integer ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INGREDIENT_NOT_FOUND,
                        "재료를 찾을 수 없습니다. ID: " + ingredientId));
    }
}