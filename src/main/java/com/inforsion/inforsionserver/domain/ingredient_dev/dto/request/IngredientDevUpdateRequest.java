package com.inforsion.inforsionserver.domain.ingredient_dev.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDevUpdateRequest {

    @Size(min = 1, max = 100, message = "재료명은 1~100자여야 합니다.")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "재고 가격은 0보다 커야 합니다.")
    @Digits(integer = 12, fraction = 2, message = "재고 가격은 소수점 2자리까지 입력 가능합니다.")
    private BigDecimal stockPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "1개당 재고 용량은 0보다 커야 합니다.")
    @Digits(integer = 10, fraction = 2, message = "1개당 재고 용량은 소수점 2자리까지 입력 가능합니다.")
    private BigDecimal unitCapacity;

    @Positive(message = "재고 수는 0보다 커야 합니다.")
    private Integer stockQuantity;
}
