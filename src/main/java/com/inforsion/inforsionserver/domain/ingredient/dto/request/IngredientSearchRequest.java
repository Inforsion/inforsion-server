package com.inforsion.inforsionserver.domain.ingredient.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Positive;

@Schema(description = "재료 검색 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientSearchRequest {

    @Schema(description = "상품 ID", example = "1")
    @Positive(message = "상품 ID는 양수여야 합니다")
    private Integer productId;

    @Schema(description = "재고 ID", example = "1")
    @Positive(message = "재고 ID는 양수여야 합니다")
    private Integer inventoryId;

    @Schema(description = "단위", example = "g")
    private String unit;

    @Schema(description = "활성화 상태", example = "true")
    private Boolean isActive;
}
