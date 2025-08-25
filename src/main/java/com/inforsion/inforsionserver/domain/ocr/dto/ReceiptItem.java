package com.inforsion.inforsionserver.domain.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItem {
    
    private String productName;
    
    private Integer quantity;
    
    private Integer unitPrice;
    
    private Integer totalPrice;
    
    private String originalText;
}