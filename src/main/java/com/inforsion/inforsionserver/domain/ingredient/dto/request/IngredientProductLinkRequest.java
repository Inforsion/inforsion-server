package com.inforsion.inforsionserver.domain.ingredient.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "재료에 상품을 연결하기 위한 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientProductLinkRequest {

    @Schema(description = "연결할 상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "상품 ID는 필수입니다")
    @Positive(message = "상품 ID는 양수여야 합니다")
    private Integer productId;

    @Schema(description = "상품 1개당 필요한 재료량", example = "20.0")
    @NotNull(message = "재료량은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "재료량은 0보다 커야 합니다")
    @Digits(integer = 10, fraction = 2, message = "재료량은 소수점 2자리까지 입력 가능합니다")
    private BigDecimal amountPerProduct;

    @Schema(description = "단위", example = "g")
    @NotBlank(message = "단위는 필수입니다")
    @Size(max = 20, message = "단위는 20자 이하여야 합니다")
    private String unit;

    @Schema(description = "재료 설명", example = "레시피에 맞춰 상품에 연결")
    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;
}
