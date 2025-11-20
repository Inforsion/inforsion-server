package com.inforsion.inforsionserver.domain.ingredient.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Schema(description = "재료 생성 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientCreateRequest {

    @Schema(description = "기존 재고 ID (없으면 newInventory로 신규 생성)", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Positive(message = "재고 ID는 양수여야 합니다")
    private Integer inventoryId;

    @Schema(description = "새로운 재고 생성 정보 (inventoryId가 없을 때 사용)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private IngredientInventoryCreateRequest newInventory;

    @Schema(description = "상품 1개당 필요한 재료량 (상품과 바로 연결 시 필수)", example = "15.5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @DecimalMin(value = "0.0", inclusive = false, message = "재료량은 0보다 커야 합니다")
    @Digits(integer = 10, fraction = 2, message = "재료량은 소수점 2자리까지 입력 가능합니다")
    private BigDecimal amountPerProduct;

    @Schema(description = "단위 (상품과 바로 연결 시 필수)", example = "g", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 20, message = "단위는 20자 이하여야 합니다")
    private String unit;

    @Schema(description = "재료 설명", example = "고급 아라비카 원두")
    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;

    @Schema(description = "상품 ID (나중에 연결 가능)", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Positive(message = "상품 ID는 양수여야 합니다")
    private Integer productId;
}
