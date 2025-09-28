package com.inforsion.inforsionserver.domain.ocr.service;

import com.inforsion.inforsionserver.domain.ocr.dto.ReceiptItem;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptAnalysisService {

    private final ProductRepository productRepository;

    // 가격 패턴만 사용
    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d{1,3}(?:,\\d{3})*)");

    /**
     * OCR 텍스트에서 제품명과 가격만 인식하여 ReceiptItem 추출
     */
    @Cacheable(value = "receiptAnalysis", key = "#extractedLines.hashCode() + '_' + #storeId")
    public List<ReceiptItem> extractReceiptItems(List<String> extractedLines, Integer storeId) {
        // 해당 매장의 제품들만 로드
        List<ProductEntity> storeProducts = productRepository.findByStoreId(storeId);
        log.info("매장 ID {}에서 {}개의 제품을 로드했습니다.", storeId, storeProducts.size());

        List<ReceiptItem> items = new ArrayList<>();

        for (String line : extractedLines) {
            ReceiptItem item = parseLineWithProductMatch(line, storeProducts);
            if (item != null) {
                items.add(item);
                log.debug("파싱 결과: {} - {}", item.getProductName(), item.getTotalPrice());
            }
        }

        log.info("영수증에서 총 {}개의 상품 항목을 추출했습니다.", items.size());
        return items;
    }
    
    /**
     * products 테이블의 제품명을 기반으로 라인 파싱
     */
    private ReceiptItem parseLineWithProductMatch(String line, List<ProductEntity> storeProducts) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String cleanLine = line.trim().replace("\n", "");

        // 1. 제품명 매칭 시도
        ProductEntity matchedProduct = findMatchingProduct(cleanLine, storeProducts);

        if (matchedProduct != null) {
            // 2. 가격 추출 (영수증에서)
            Integer priceFromReceipt = extractPriceFromLine(cleanLine);

            return ReceiptItem.builder()
                    .productName(matchedProduct.getName())
                    .quantity(1) // 기본 수량 1
                    .unitPrice(priceFromReceipt != null ? priceFromReceipt : matchedProduct.getPrice().intValue())
                    .totalPrice(priceFromReceipt != null ? priceFromReceipt : matchedProduct.getPrice().intValue())
                    .originalText(cleanLine)
                    .build();
        }

        return null;
    }

    /**
     * products 테이블에서 제품명 매칭
     */
    private ProductEntity findMatchingProduct(String line, List<ProductEntity> storeProducts) {
        String normalizedLine = normalizeText(line);

        // 1. 완전 일치 검색
        for (ProductEntity product : storeProducts) {
            String normalizedProductName = normalizeText(product.getName());
            if (normalizedLine.contains(normalizedProductName)) {
                log.debug("제품 매칭: {} -> {}", line, product.getName());
                return product;
            }
        }

        // 2. 부분 일치 검색 (주요 단어 기준)
        for (ProductEntity product : storeProducts) {
            String normalizedProductName = normalizeText(product.getName());
            String[] productWords = normalizedProductName.split("\\s+");

            boolean hasMainWord = false;
            for (String productWord : productWords) {
                if (productWord.length() >= 2 && normalizedLine.contains(productWord)) {
                    hasMainWord = true;
                    break;
                }
            }

            if (hasMainWord) {
                log.debug("부분 매칭: {} -> {}", line, product.getName());
                return product;
            }
        }

        return null;
    }

    /**
     * 텍스트 정규화
     */
    private String normalizeText(String text) {
        if (text == null) return "";
        return text.trim()
                .replaceAll("[*\\-=()\\[\\]{}.,]", "")
                .replaceAll("\\s+", "")
                .toLowerCase();
    }

    /**
     * 라인에서 가격 추출
     */
    private Integer extractPriceFromLine(String line) {
        Matcher matcher = PRICE_PATTERN.matcher(line);

        while (matcher.find()) {
            String priceStr = matcher.group(1).replace(",", "");
            try {
                int price = Integer.parseInt(priceStr);
                // 합리적인 가격 범위 (100원 ~ 100만원)
                if (price >= 100 && price <= 1000000) {
                    return price;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return null;
    }
}