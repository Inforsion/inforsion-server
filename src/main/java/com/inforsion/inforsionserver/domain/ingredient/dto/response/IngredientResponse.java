package com.inforsion.inforsionserver.domain.ingredient.dto.response;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "재료 응답")
@Getter
@Builder
public class IngredientResponse {

    @Schema(description = "재료 ID", example = "1")
    private final Integer id;

    @Schema(description = "매장 ID", example = "1")
    private final Integer storeId;

    @Schema(description = "매장명", example = "강남점")
    private final String storeName;

    @Schema(description = "재료명", example = "우유")
    private final String name;

    @Schema(description = "단위", example = "ml")
    private final String unit;

    @Schema(description = "기본 유통기한(일)", example = "7")
    private final Integer defaultExpiryDays;

    @Schema(description = "재료 설명", example = "서울우유 1L")
    private final String description;

    @Schema(description = "이미지 URL")
    private final String imageUrl;

    @Schema(description = "활성 상태", example = "true")
    private final Boolean isActive;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private final LocalDateTime updatedAt;

    public static IngredientResponse from(IngredientEntity entity) {
        return IngredientResponse.builder()
                .id(entity.getId())
                .storeId(entity.getStore() != null ? entity.getStore().getId() : null)
                .storeName(entity.getStore() != null ? entity.getStore().getName() : null)
                .name(entity.getName())
                .unit(entity.getUnit())
                .defaultExpiryDays(entity.getDefaultExpiryDays())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}