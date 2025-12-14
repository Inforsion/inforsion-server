package com.inforsion.inforsionserver.domain.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "재고 생성 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCreateRequest {

    @Schema(description = "매장 ID", example = "1", required = true)
    @NotNull(message = "매장 ID는 필수입니다.")
    private Integer storeId;

    @Schema(description = "재료 ID", example = "1", required = true)
    @NotNull(message = "재료 ID는 필수입니다.")
    private Integer ingredientId;

    @Schema(description = "현재 재고량", example = "1000.00", required = true)
    @NotNull(message = "현재 재고량은 필수입니다.")
    private BigDecimal currentStock;

    @Schema(description = "최소 재고 수준", example = "100.00")
    private BigDecimal minStock;

    @Schema(description = "최대 재고 수준", example = "5000.00")
    private BigDecimal maxStock;

    @Schema(description = "단위 당 가격", example = "3000", required = true)
    @NotNull(message = "단위 당 가격은 필수입니다.")
    private BigDecimal unitCost;

    @Schema(description = "유통기한", example = "2025-12-31", required = true)
    @NotNull(message = "유통기한은 필수입니다.")
    private LocalDate expiryDate;

    @Schema(description = "마지막 입고일", example = "2025-12-14")
    private LocalDate lastRestockedDate;
}