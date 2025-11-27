package com.inforsion.inforsionserver.domain.ingredient_dev.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDevCreateRequest {

    @NotBlank(message = "재료명은 필수입니다.")
    private String name;

    @NotNull(message = "재고 가격은 필수입니다.")
    @Positive(message = "재고 가격은 0보다 커야 합니다.")
    private BigDecimal stockPrice;

    @NotNull(message = "1개당 재고 용량은 필수입니다.")
    @Positive(message = "1개당 재고 용량은 0보다 커야 합니다.")
    private BigDecimal unitCapacity;

    @NotNull(message = "재고 수는 필수입니다.")
    @Positive(message = "재고 수는 0보다 커야 합니다.")
    private Integer stockQuantity;
}
