package com.inforsion.inforsionserver.domain.ingredient.dto.response;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
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

    @Schema(description = "재료명")
    private String name;

    @Schema(description = "상품 1개당 필요한 재료량")
    private BigDecimal amountPerProduct;

    @Schema(description = "단위")
    private String unit;

    @Schema(description = "재료 설명")
    private String description;

    @Schema(description = "재료 이미지 URL")
    private String imageUrl;

    @Schema(description = "원본 파일명")
    private String originalFileName;

    @Schema(description = "S3 키 (내부 관리용)")
    private String s3Key;

    @Schema(description = "이미지 보유 여부")
    private Boolean hasImage;

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
        return IngredientResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .amountPerProduct(entity.getAmountPerProduct())
                .unit(entity.getUnit())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .originalFileName(entity.getOriginalFileName())
                .s3Key(entity.getS3Key())
                .hasImage(entity.hasImage())
                .isActive(entity.getIsActive())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .storeId(entity.getProduct().getStore().getId())
                .storeName(entity.getProduct().getStore().getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
