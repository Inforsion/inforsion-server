package com.inforsion.inforsionserver.domain.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMatchingResultDto {
    
    private Integer rawDataId;
    private List<MatchedItemDto> matchedItems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatchedItemDto {
        private String ocrItemName;
        private Integer quantity;
        private Integer price;
        private Integer totalAmount;
        
        // 매칭된 제품 후보들
        private List<ProductCandidateDto> productCandidates;
        
        // 사용자가 선택한 제품 (null이면 아직 미선택)
        private Integer selectedProductId;
        @Builder.Default
        private Boolean confirmed = false;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductCandidateDto {
        private Integer productId;
        private String productName;
        private BigDecimal price;
        private Double similarityScore; // 유사도 점수 (0.0 ~ 1.0)
        @Builder.Default
        private Boolean exactMatch = false;
    }
}