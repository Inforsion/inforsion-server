package com.inforsion.inforsionserver.domain.ocr.dto;

import com.inforsion.inforsionserver.global.enums.DocumentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMatchingResultDtoTest {

    @Test
    void matchedItemBuilder_setsDefaultsAndValues() {
        ProductMatchingResultDto.ProductCandidateDto candidate = ProductMatchingResultDto.ProductCandidateDto.builder()
                .productId(10)
                .productName("Americano")
                .price(BigDecimal.valueOf(4500))
                .similarityScore(0.92)
                .exactMatch(true)
                .build();

        ProductMatchingResultDto.MatchedItemDto matchedItem = ProductMatchingResultDto.MatchedItemDto.builder()
                .ocrItemName("아메리카노")
                .quantity(2)
                .price(4500)
                .totalAmount(9000)
                .productCandidates(List.of(candidate))
                .selectedProductId(10)
                .confirmed(false)
                .build();

        assertThat(matchedItem.getOcrItemName()).isEqualTo("아메리카노");
        assertThat(matchedItem.getQuantity()).isEqualTo(2);
        assertThat(matchedItem.getProductCandidates()).containsExactly(candidate);
        assertThat(matchedItem.getConfirmed()).isFalse();
        assertThat(candidate.getExactMatch()).isTrue();
    }

    @Test
    void processingRequestDto_builderStoresValues() {
        LocalDateTime now = LocalDateTime.now();

        OcrProcessingRequestDto requestDto = OcrProcessingRequestDto.builder()
                .storeId(1)
                .documentType(DocumentType.SALES_RECEIPT)
                .rawOcrText("TEST")
                .parsedItems(List.of())
                .supplierName("Supplier A")
                .documentDate(now)
                .build();

        assertThat(requestDto.getStoreId()).isEqualTo(1);
        assertThat(requestDto.getDocumentType()).isEqualTo(DocumentType.SALES_RECEIPT);
        assertThat(requestDto.getParsedItems()).isEmpty();
        assertThat(requestDto.getDocumentDate()).isEqualTo(now);
    }

    @Test
    void confirmationRequestDto_handlesInventoryAndProductIds() {
        OcrConfirmationRequestDto.ConfirmedItemDto confirmedItem = OcrConfirmationRequestDto.ConfirmedItemDto.builder()
                .ocrItemName("원두")
                .quantity(1)
                .price(10000)
                .totalAmount(10000)
                .selectedProductId(5)
                .selectedInventoryId(8)
                .correctedItemName("콜드브루 원두")
                .build();

        OcrConfirmationRequestDto requestDto = OcrConfirmationRequestDto.builder()
                .rawDataId(1234)
                .confirmedItems(List.of(confirmedItem))
                .build();

        assertThat(requestDto.getRawDataId()).isEqualTo(1234);
        assertThat(requestDto.getConfirmedItems()).hasSize(1);
        OcrConfirmationRequestDto.ConfirmedItemDto item = requestDto.getConfirmedItems().get(0);
        assertThat(item.getSelectedProductId()).isEqualTo(5);
        assertThat(item.getSelectedInventoryId()).isEqualTo(8);
        assertThat(item.getCorrectedItemName()).isEqualTo("콜드브루 원두");
    }
}
