package com.inforsion.inforsionserver.domain.ingredient.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Positive;

@Schema(description = "재료 검색 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientSearchRequest {

    @Schema(description = "재료명 (부분 검색)", example = "원두")
    private String name;

    @Schema(description = "상품 ID", example = "1")
    @Positive(message = "상품 ID는 양수여야 합니다")
    private Integer productId;

    @Schema(description = "가게 ID", example = "1")
    @Positive(message = "가게 ID는 양수여야 합니다")
    private Integer storeId;

    @Schema(description = "단위", example = "g")
    private String unit;

    @Schema(description = "활성화 상태", example = "true")
    private Boolean isActive;
}
