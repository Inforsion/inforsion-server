package com.inforsion.inforsionserver.domain.ingredient_dev.service;

import com.inforsion.inforsionserver.domain.ingredient_dev.dto.request.IngredientDevCreateRequest;
import com.inforsion.inforsionserver.domain.ingredient_dev.dto.request.IngredientDevUpdateRequest;
import com.inforsion.inforsionserver.domain.ingredient_dev.dto.response.IngredientDevResponse;
import com.inforsion.inforsionserver.domain.ingredient_dev.entity.IngredientDevEntity;
import com.inforsion.inforsionserver.domain.ingredient_dev.repository.IngredientDevRepository;
import com.inforsion.inforsionserver.global.error.code.ErrorCode;
import com.inforsion.inforsionserver.global.error.exception.BusinessException;
import com.inforsion.inforsionserver.global.service.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientDevService {

    private final IngredientDevRepository ingredientDevRepository;
    private final S3FileUploadService s3FileUploadService;

    @Transactional
    public IngredientDevResponse createIngredient(IngredientDevCreateRequest request /*, MultipartFile imageFile */) {
        if (ingredientDevRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.INGREDIENT_ALREADY_EXISTS, "이미 존재하는 재료명입니다.");
        }

        IngredientDevEntity ingredient = IngredientDevEntity.builder()
                .name(request.getName())
                .stockPrice(request.getStockPrice())
                .unitCapacity(request.getUnitCapacity())
                .stockQuantity(request.getStockQuantity())
                .build();

        IngredientDevEntity saved = ingredientDevRepository.save(ingredient);

        // 이미지 업로드는 추후 활성화
        // if (imageFile != null && !imageFile.isEmpty()) {
        //     String imageUrl = s3FileUploadService.uploadImageFile(imageFile, "ingredients");
        //     saved.updateImageUrl(imageUrl);
        // }

        return IngredientDevResponse.from(saved);
    }

    @Transactional
    public IngredientDevResponse updateIngredient(Integer ingredientId, IngredientDevUpdateRequest request) {
        IngredientDevEntity ingredient = getIngredientOrThrow(ingredientId);

        if (request.getName() != null && !request.getName().equals(ingredient.getName())) {
            if (ingredientDevRepository.existsByName(request.getName())) {
                throw new BusinessException(ErrorCode.INGREDIENT_ALREADY_EXISTS, "이미 존재하는 재료명입니다.");
            }
        }

        ingredient.update(
                request.getName(),
                request.getStockPrice(),
                request.getUnitCapacity(),
                request.getStockQuantity()
        );

        return IngredientDevResponse.from(ingredient);
    }

    public IngredientDevResponse getIngredient(Integer ingredientId) {
        IngredientDevEntity ingredient = getIngredientOrThrow(ingredientId);
        return IngredientDevResponse.from(ingredient);
    }

    @Transactional
    public void deleteIngredient(Integer ingredientId) {
        IngredientDevEntity ingredient = getIngredientOrThrow(ingredientId);
        // 이미지 파일은 별도 관리 지시에 따라 S3 정리하지 않는다.
        ingredientDevRepository.delete(ingredient);
    }

    /**
     * 이미지 업로드 (기존 이미지가 있다면 유지)
     */
    @Transactional
    public IngredientDevResponse uploadImage(Integer ingredientId, MultipartFile imageFile) {
        IngredientDevEntity ingredient = getIngredientOrThrow(ingredientId);
        String imageUrl = s3FileUploadService.uploadImageFile(imageFile, "ingredients-dev");
        ingredient.updateImageUrl(imageUrl);
        return IngredientDevResponse.from(ingredient);
    }

    /**
     * 이미지 수정 (기존 이미지를 S3에서 제거 후 새로 업로드)
     */
    @Transactional
    public IngredientDevResponse updateImage(Integer ingredientId, MultipartFile imageFile) {
        IngredientDevEntity ingredient = getIngredientOrThrow(ingredientId);
        s3FileUploadService.deleteFile(ingredient.getImageUrl());
        String imageUrl = s3FileUploadService.uploadImageFile(imageFile, "ingredients-dev");
        ingredient.updateImageUrl(imageUrl);
        return IngredientDevResponse.from(ingredient);
    }

    /**
     * 이미지 조회 (URL 반환)
     */
    public IngredientDevResponse getImage(Integer ingredientId) {
        IngredientDevEntity ingredient = getIngredientOrThrow(ingredientId);
        if (ingredient.getImageUrl() == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND, "해당 재료에 등록된 이미지가 없습니다.");
        }
        return IngredientDevResponse.from(ingredient);
    }

    /**
     * 이미지 삭제 (S3와 DB 필드 동시 정리)
     */
    @Transactional
    public void deleteImage(Integer ingredientId) {
        IngredientDevEntity ingredient = getIngredientOrThrow(ingredientId);
        s3FileUploadService.deleteFile(ingredient.getImageUrl());
        ingredient.updateImageUrl(null);
    }

    private IngredientDevEntity getIngredientOrThrow(Integer ingredientId) {
        return ingredientDevRepository.findById(ingredientId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INGREDIENT_NOT_FOUND,
                        "재료를 찾을 수 없습니다. ID: " + ingredientId));
    }
}
