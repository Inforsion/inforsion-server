package com.inforsion.inforsionserver.domain.ocr.dto;

import com.inforsion.inforsionserver.domain.ocr.mongo.entity.OcrRawDataEntity;
import com.inforsion.inforsionserver.global.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OCR 로우 데이터 응답 DTO
 * MongoDB에 저장된 원본 OCR 데이터 조회용
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrRawDataResponseDto {

    private String mongoId;
    private Integer rawDataId;
    private Integer storeId;
    private DocumentType documentType;
    private String rawOcrText;
    private String parsedItem;
    private String supplierName;
    private LocalDateTime documentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 로그 확인용 추가 정보
    private Integer textLength;
    private Integer lineCount;
    private List<String> textLines;

    public static OcrRawDataResponseDto from(OcrRawDataEntity entity) {
        String rawText = entity.getRawOcrText() != null ? entity.getRawOcrText() : "";
        String[] lines = rawText.split("\n");

        return OcrRawDataResponseDto.builder()
                .mongoId(entity.getId())
                .rawDataId(entity.getRawDataId())
                .storeId(entity.getStoreId())
                .documentType(entity.getDocumentType())
                .rawOcrText(rawText)
                .parsedItem(entity.getParsedItem())
                .supplierName(entity.getSupplierName())
                .documentDate(entity.getDocumentDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .textLength(rawText.length())
                .lineCount(lines.length)
                .textLines(List.of(lines))
                .build();
    }
}