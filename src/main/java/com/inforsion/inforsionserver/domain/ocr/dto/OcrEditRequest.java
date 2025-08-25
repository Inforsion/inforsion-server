package com.inforsion.inforsionserver.domain.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrEditRequest {

    @NotBlank(message = "MongoDB OCR 결과 ID는 필수입니다")
    private String ocrResultId;

    @NotBlank(message = "수정된 텍스트는 필수입니다")
    private String editedText;

    private List<String> editedLines;

    private String notes;
    
    private List<ReceiptItem> receiptItems;

    @NotNull(message = "사용자 ID는 필수입니다")
    private Integer userId;
}