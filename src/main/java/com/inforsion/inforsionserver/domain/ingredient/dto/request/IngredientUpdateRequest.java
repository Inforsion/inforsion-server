package com.inforsion.inforsionserver.domain.ingredient.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "재료 수정 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientUpdateRequest {

    @Schema(description = "재료명", example = "우유")
    private String name;

    @Schema(description = "재고 가격", example = "3000.00")
    private BigDecimal stockPrice;

    @Schema(description = "1개당 재고 용량", example = "1000.00")
    private BigDecimal unitCapacity;

    @Schema(description = "재고 수", example = "10")
    private Integer stockQuantity;
}