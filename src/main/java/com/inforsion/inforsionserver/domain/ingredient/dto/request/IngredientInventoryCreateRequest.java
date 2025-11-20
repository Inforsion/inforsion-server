package com.inforsion.inforsionserver.domain.ingredient.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "재료 등록 시 함께 생성할 재고 정보")
@Getter
@NoArgsConstructor
public class IngredientInventoryCreateRequest {

    @Schema(description = "재고명", example = "에티오피아 원두")
    @NotBlank(message = "재고명은 필수입니다")
    private String name;

    @Schema(description = "현재 재고 수량", example = "0")
    @NotNull(message = "현재 재고 수량은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = true, message = "현재 재고 수량은 0 이상이어야 합니다")
    @Digits(integer = 10, fraction = 2, message = "현재 재고 수량은 소수점 2자리까지 입력 가능합니다")
    private BigDecimal currentStock;

    @Schema(description = "최소 재고 수량", example = "10.0")
    @DecimalMin(value = "0.0", inclusive = true, message = "최소 재고 수량은 0 이상이어야 합니다")
    @Digits(integer = 10, fraction = 2, message = "최소 재고 수량은 소수점 2자리까지 입력 가능합니다")
    private BigDecimal minStock;

    @Schema(description = "최대 재고 수량", example = "200.0")
    @DecimalMin(value = "0.0", inclusive = true, message = "최대 재고 수량은 0 이상이어야 합니다")
    @Digits(integer = 10, fraction = 2, message = "최대 재고 수량은 소수점 2자리까지 입력 가능합니다")
    private BigDecimal maxStock;

    @Schema(description = "단위", example = "g")
    @NotBlank(message = "재고 단위는 필수입니다")
    @Size(max = 20, message = "재고 단위는 20자 이하여야 합니다")
    private String unit;

    @Schema(description = "단위 당 가격", example = "1500.00")
    @NotNull(message = "단위 당 가격은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = true, message = "단위 당 가격은 0 이상이어야 합니다")
    @Digits(integer = 10, fraction = 2, message = "단위 당 가격은 소수점 2자리까지 입력 가능합니다")
    private BigDecimal unitCost;

    @Schema(description = "유통기한", example = "2025-12-31")
    @NotNull(message = "유통기한은 필수입니다")
    private LocalDate expiryDate;

    @Schema(description = "마지막 입고일", example = "2025-01-10")
    private LocalDate lastRestockedDate;

    @Schema(description = "재고가 속할 매장 ID (미제공 시 상품의 매장으로 설정)", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Positive(message = "매장 ID는 양수여야 합니다")
    private Integer storeId;
}
