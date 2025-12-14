package com.inforsion.inforsionserver.domain.ingredient.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "재료 수정 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientUpdateRequest {

    @Schema(description = "재료명", example = "우유")
    private String name;

    @Schema(description = "단위", example = "ml")
    private String unit;

    @Schema(description = "기본 유통기한(일)", example = "7")
    private Integer defaultExpiryDays;

    @Schema(description = "활성 상태", example = "true")
    private Boolean isActive;
}