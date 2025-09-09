package com.inforsion.inforsionserver.domain.ocr.dto;

import com.inforsion.inforsionserver.global.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrProcessingRequestDto {
    
    private Integer storeId;
    private DocumentType documentType;
    private String rawOcrText;
    private List<OcrItemDto> parsedItems;
    private String supplierName;
    private LocalDateTime documentDate;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OcrItemDto {
        private String itemName;
        private Integer quantity;
        private Integer price;
        private Integer totalAmount;
    }
}