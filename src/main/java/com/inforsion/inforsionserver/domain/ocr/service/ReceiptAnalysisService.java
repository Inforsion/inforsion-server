package com.inforsion.inforsionserver.domain.ocr.service;

import com.inforsion.inforsionserver.domain.ocr.dto.ReceiptItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ReceiptAnalysisService {
    
    private static final Pattern PRICE_PATTERN = Pattern.compile("([0-9,]+)원?");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("(\\d+)개|x(\\d+)|\\*(\\d+)|(\\d+)EA|(\\d+)ea");
    
    @Cacheable(value = "receiptAnalysis", key = "#extractedLines.hashCode()")
    public List<ReceiptItem> extractReceiptItems(List<String> extractedLines) {
        List<ReceiptItem> items = new ArrayList<>();
        
        for (String line : extractedLines) {
            if (isReceiptItemLine(line)) {
                ReceiptItem item = parseReceiptLine(line);
                if (item != null && item.getProductName() != null && !item.getProductName().trim().isEmpty()) {
                    items.add(item);
                }
            }
        }
        
        log.info("영수증에서 총 {}개의 상품 항목을 추출했습니다.", items.size());
        return items;
    }
    
    private boolean isReceiptItemLine(String line) {
        if (line == null || line.trim().isEmpty()) return false;
        
        String trimmedLine = line.trim();
        
        // 제외할 라인들 (헤더, 푸터, 총합 등)
        if (trimmedLine.contains("영수증") || 
            trimmedLine.contains("합계") || 
            trimmedLine.contains("총액") ||
            trimmedLine.contains("받은돈") ||
            trimmedLine.contains("거스름돈") ||
            trimmedLine.contains("카드") ||
            trimmedLine.contains("현금") ||
            trimmedLine.contains("사업자") ||
            trimmedLine.contains("전화") ||
            trimmedLine.contains("주소") ||
            trimmedLine.contains("대표") ||
            trimmedLine.contains("TEL") ||
            trimmedLine.matches("^[0-9\\-\\s:]+$") || // 날짜/시간만 있는 라인
            trimmedLine.matches("^\\*+$") || // 별표만 있는 라인
            trimmedLine.matches("^=+$") || // 등호만 있는 라인
            trimmedLine.matches("^-+$")) { // 대시만 있는 라인
            return false;
        }
        
        // 가격이 포함된 라인을 상품 라인으로 간주
        return PRICE_PATTERN.matcher(trimmedLine).find() && 
               !trimmedLine.matches("^[0-9,원\\s]+$"); // 가격만 있는 라인은 제외
    }
    
    private ReceiptItem parseReceiptLine(String line) {
        try {
            String originalLine = line.trim();
            
            // 가격 추출
            List<Integer> prices = extractPrices(originalLine);
            
            // 수량 추출
            Integer quantity = extractQuantity(originalLine);
            if (quantity == null) {
                quantity = 1; // 기본값
            }
            
            // 상품명 추출 (가격과 수량 정보를 제거한 나머지)
            String productName = extractProductName(originalLine);
            
            if (productName == null || productName.trim().isEmpty()) {
                return null;
            }
            
            // 가격 정보 설정
            Integer unitPrice = null;
            Integer totalPrice = null;
            
            if (prices.size() == 1) {
                // 가격이 하나만 있는 경우
                if (quantity > 1) {
                    totalPrice = prices.get(0);
                    unitPrice = totalPrice / quantity;
                } else {
                    unitPrice = prices.get(0);
                    totalPrice = unitPrice;
                }
            } else if (prices.size() >= 2) {
                // 가격이 두 개 이상 있는 경우 (단가, 총액)
                unitPrice = prices.get(0);
                totalPrice = prices.get(1);
            }
            
            return ReceiptItem.builder()
                    .productName(productName)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .originalText(originalLine)
                    .build();
                    
        } catch (Exception e) {
            log.warn("영수증 라인 파싱 실패: {}, 오류: {}", line, e.getMessage());
            return null;
        }
    }
    
    private List<Integer> extractPrices(String line) {
        List<Integer> prices = new ArrayList<>();
        Matcher matcher = PRICE_PATTERN.matcher(line);
        
        while (matcher.find()) {
            try {
                String priceStr = matcher.group(1).replace(",", "");
                int price = Integer.parseInt(priceStr);
                if (price > 0 && price < 1000000) { // 합리적인 가격 범위
                    prices.add(price);
                }
            } catch (NumberFormatException e) {
                // 무시
            }
        }
        
        return prices;
    }
    
    private Integer extractQuantity(String line) {
        Matcher matcher = QUANTITY_PATTERN.matcher(line.toLowerCase());
        
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String quantityStr = matcher.group(i);
                if (quantityStr != null) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        if (quantity > 0 && quantity <= 100) { // 합리적인 수량 범위
                            return quantity;
                        }
                    } catch (NumberFormatException e) {
                        // 무시
                    }
                }
            }
        }
        
        return null;
    }
    
    private String extractProductName(String line) {
        String productName = line;
        
        // 가격 정보 제거
        productName = PRICE_PATTERN.matcher(productName).replaceAll("");
        
        // 수량 정보 제거
        productName = QUANTITY_PATTERN.matcher(productName).replaceAll("");
        
        // 특수 문자 및 여분의 공백 정리
        productName = productName.replaceAll("[*\\-=]+", " ");
        productName = productName.replaceAll("\\s+", " ");
        productName = productName.trim();
        
        // 너무 짧은 상품명 필터링
        if (productName.length() < 2) {
            return null;
        }
        
        return productName;
    }
}