package com.inforsion.inforsionserver.domain.ingredient.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Schema(description = "재료 수정 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientUpdateRequest {

    @Schema(description = "재고 ID", example = "2")
    @Positive(message = "재고 ID는 양수여야 합니다")
    private Integer inventoryId;

    @Schema(description = "상품 1개당 필요한 재료량", example = "20.0")
    @DecimalMin(value = "0.0", inclusive = false, message = "재료량은 0보다 커야 합니다")
    @Digits(integer = 10, fraction = 2, message = "재료량은 소수점 2자리까지 입력 가능합니다")
    private BigDecimal amountPerProduct;

    @Schema(description = "단위", example = "g")
    @Size(max = 20, message = "단위는 20자 이하여야 합니다")
    private String unit;

    @Schema(description = "재료 설명", example = "프리미엄 아라비카 원두")
    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;

    @Schema(description = "활성화 상태", example = "true")
    private Boolean isActive;

}
