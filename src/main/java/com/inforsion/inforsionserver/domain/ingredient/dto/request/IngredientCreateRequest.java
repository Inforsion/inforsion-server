package com.inforsion.inforsionserver.domain.ingredient.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "재료 생성 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientCreateRequest {

    @Schema(description = "매장 ID", example = "1")
    @NotNull(message = "매장 ID는 필수입니다.")
    private Integer storeId;

    @Schema(description = "재료명", example = "우유")
    @NotBlank(message = "재료명은 필수입니다.")
    private String name;

    @Schema(description = "단위", example = "ml")
    @NotBlank(message = "단위는 필수입니다.")
    private String unit;

    @Schema(description = "기본 유통기한(일)", example = "7")
    private Integer defaultExpiryDays;
}