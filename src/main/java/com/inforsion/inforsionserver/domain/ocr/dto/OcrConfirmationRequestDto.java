package com.inforsion.inforsionserver.domain.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrConfirmationRequestDto {
    
    private Integer rawDataId;
    private List<ConfirmedItemDto> confirmedItems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConfirmedItemDto {
        private String ocrItemName;
        private Integer quantity;
        private Integer price;
        private Integer totalAmount;
        private Integer selectedProductId; // 사용자가 선택한 제품 ID
        private String correctedItemName; // 사용자가 수정한 아이템 이름 (선택사항)
    }
}