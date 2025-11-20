package com.inforsion.inforsionserver.domain.ingredient.dto.response;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "재료 정보 응답 DTO")
@Getter
@Builder
public class IngredientResponse {

    @Schema(description = "재료 ID")
    private Integer id;

    @Schema(description = "재고 ID")
    private Integer inventoryId;

    @Schema(description = "재고명")
    private String inventoryName;

    @Schema(description = "상품 1개당 필요한 재료량")
    private BigDecimal amountPerProduct;

    @Schema(description = "단위")
    private String unit;

    @Schema(description = "재료 설명")
    private String description;


    @Schema(description = "활성화 상태")
    private Boolean isActive;

    @Schema(description = "상품 ID")
    private Integer productId;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "가게 ID")
    private Integer storeId;

    @Schema(description = "가게명")
    private String storeName;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;

    /**
     * Entity에서 Response로 변환
     */
    public static IngredientResponse from(IngredientEntity entity) {
        ProductEntity product = entity.getProduct();
        InventoryEntity inventory = entity.getInventory();
        StoreEntity store = product != null ? product.getStore() : (inventory != null ? inventory.getStore() : null);

        return IngredientResponse.builder()
                .id(entity.getId())
                .inventoryId(inventory != null ? inventory.getId() : null)
                .inventoryName(inventory != null ? inventory.getName() : null)
                .amountPerProduct(entity.getAmountPerProduct())
                .unit(entity.getUnit())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : null)
                .storeId(store != null ? store.getId() : null)
                .storeName(store != null ? store.getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
