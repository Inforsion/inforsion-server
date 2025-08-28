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
    
    // 메뉴 기반 파싱을 위한 패턴들
    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d{1,3}(?:,\\d{3})*)");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("(\\d+)개?|x(\\d+)|\\*(\\d+)|(\\d+)EA|(\\d+)ea");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    
    @Cacheable(value = "receiptAnalysis", key = "#extractedLines.hashCode()")
    public List<ReceiptItem> extractReceiptItems(List<String> extractedLines) {
        // 모든 메뉴 아이템을 미리 로드 (캐싱)
        List<ProductEntity> allMenuItems = productRepository.findAll();
        log.info("데이터베이스에서 {}개의 메뉴 아이템을 로드했습니다.", allMenuItems.size());
        
        List<ReceiptItem> items = new ArrayList<>();
        
        for (String line : extractedLines) {
            if (containsPricePattern(line)) {
                ReceiptItem item = parseLineWithMenuMatch(line, allMenuItems);
                if (item != null && item.getProductName() != null && !item.getProductName().trim().isEmpty()) {
                    items.add(item);
                    log.debug("파싱 결과: {} (수량: {}, 단가: {})", 
                             item.getProductName(), item.getQuantity(), item.getUnitPrice());
                }
            }
        }
        
        log.info("영수증에서 총 {}개의 상품 항목을 추출했습니다.", items.size());
        return items;
    }
    
    /**
     * 가격 패턴이 포함된 라인인지 확인
     */
    private boolean containsPricePattern(String line) {
        if (line == null || line.trim().isEmpty()) {
            return false;
        }
        
        String cleanLine = line.trim();
        
        // 제외할 라인들
        if (cleanLine.contains("Subtotal") || 
            cleanLine.contains("Discount") || 
            cleanLine.contains("NET Amount") || 
            cleanLine.contains("Tax") || 
            cleanLine.contains("Total") ||
            cleanLine.contains("영수증") || 
            cleanLine.contains("합계") || 
            cleanLine.contains("총액") ||
            cleanLine.contains("받은돈") ||
            cleanLine.contains("거스름돈") ||
            cleanLine.contains("카드") ||
            cleanLine.contains("현금") ||
            cleanLine.contains("사업자") ||
            cleanLine.contains("전화") ||
            cleanLine.contains("주소") ||
            cleanLine.contains("대표") ||
            cleanLine.contains("TEL") ||
            cleanLine.matches("^[0-9\\-\\s:]+$") || // 날짜/시간만
            cleanLine.matches("^\\*+$")) { // 별표만
            return false;
        }
        
        // 가격 패턴이 있는지 확인
        return PRICE_PATTERN.matcher(cleanLine).find();
    }
    
    /**
     * 메뉴 데이터베이스를 참고하여 라인 파싱
     */
    private ReceiptItem parseLineWithMenuMatch(String line, List<ProductEntity> menuItems) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String cleanLine = line.trim().replace("\n", "");
        
        // 1. 메뉴 이름 매칭 시도
        ProductEntity matchedProduct = findBestMenuMatch(cleanLine, menuItems);
        
        if (matchedProduct != null) {
            // 2. 수량 추출
            Integer quantity = extractQuantityFromLine(cleanLine);
            if (quantity == null) quantity = 1;
            
            // 3. 가격 정보 추출
            List<Integer> prices = extractPricesFromLine(cleanLine);
            Integer unitPrice = matchedProduct.getPrice().intValue();
            Integer totalPrice = null;
            
            // 추출된 가격이 있다면 사용, 없다면 DB 가격 사용
            if (!prices.isEmpty()) {
                if (prices.size() == 1) {
                    totalPrice = prices.get(0);
                    // 수량이 1보다 크면 단가 계산
                    if (quantity > 1) {
                        unitPrice = totalPrice / quantity;
                    } else {
                        unitPrice = totalPrice;
                    }
                } else {
                    // 첫 번째는 단가, 마지막은 총액으로 간주
                    unitPrice = prices.get(0);
                    totalPrice = prices.get(prices.size() - 1);
                }
            } else {
                totalPrice = unitPrice * quantity;
            }
            
            return ReceiptItem.builder()
                    .productName(matchedProduct.getName())
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .originalText(cleanLine)
                    .build();
        }
        
        // 4. 매칭되지 않으면 기존 방식으로 fallback
        return parseWithoutMenuMatch(cleanLine);
    }
    
    /**
     * 메뉴 아이템에서 가장 유사한 제품 찾기
     */
    private ProductEntity findBestMenuMatch(String line, List<ProductEntity> menuItems) {
        String cleanLine = normalizeText(line);
        
        // 1. 완전 일치 검색
        for (ProductEntity item : menuItems) {
            String menuName = normalizeText(item.getName());
            if (cleanLine.contains(menuName) || menuName.contains(cleanLine)) {
                log.debug("완전 매칭: {} -> {}", line, item.getName());
                return item;
            }
        }
        
        // 2. 부분 일치 검색 (단어 단위)
        for (ProductEntity item : menuItems) {
            String menuName = normalizeText(item.getName());
            String[] menuWords = menuName.split("\\s+");
            String[] lineWords = cleanLine.split("\\s+");
            
            int matchCount = 0;
            for (String menuWord : menuWords) {
                for (String lineWord : lineWords) {
                    if (menuWord.equals(lineWord) || menuWord.contains(lineWord) || lineWord.contains(menuWord)) {
                        matchCount++;
                        break;
                    }
                }
            }
            
            // 50% 이상 매칭되면 선택
            if (matchCount >= Math.max(1, menuWords.length / 2)) {
                log.debug("부분 매칭: {} -> {} (매칭도: {}/{})", line, item.getName(), matchCount, menuWords.length);
                return item;
            }
        }
        
        return null;
    }
    
    /**
     * 텍스트 정규화 (공백, 특수문자 제거)
     */
    private String normalizeText(String text) {
        if (text == null) return "";
        return text.trim()
                   .replaceAll("[*\\-=()\\[\\]{}]", " ")
                   .replaceAll("\\s+", " ")
                   .toLowerCase();
    }
    
    /**
     * 라인에서 수량 추출
     */
    private Integer extractQuantityFromLine(String line) {
        Matcher matcher = QUANTITY_PATTERN.matcher(line.toLowerCase());
        
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String quantityStr = matcher.group(i);
                if (quantityStr != null && !quantityStr.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        if (quantity > 0 && quantity <= 100) {
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
    
    /**
     * 라인에서 가격들 추출
     */
    private List<Integer> extractPricesFromLine(String line) {
        List<Integer> prices = new ArrayList<>();
        Matcher matcher = PRICE_PATTERN.matcher(line);
        
        while (matcher.find()) {
            String priceStr = matcher.group(1).replace(",", "");
            try {
                int price = Integer.parseInt(priceStr);
                if (price >= 100 && price < 1000000) { // 합리적인 가격 범위
                    prices.add(price);
                }
            } catch (NumberFormatException e) {
                // 무시
            }
        }
        
        return prices;
    }
    
    /**
     * 메뉴 매칭 없이 기존 방식으로 파싱 (fallback)
     */
    private ReceiptItem parseWithoutMenuMatch(String line) {
        try {
            List<Integer> prices = extractPricesFromLine(line);
            if (prices.isEmpty()) {
                return null;
            }
            
            Integer quantity = extractQuantityFromLine(line);
            if (quantity == null) quantity = 1;
            
            // 상품명 추출 (가격과 수량 정보 제거)
            String productName = line;
            productName = PRICE_PATTERN.matcher(productName).replaceAll(" ");
            productName = QUANTITY_PATTERN.matcher(productName).replaceAll(" ");
            productName = productName.replaceAll("[*\\-=()\\[\\]{}]+", " ");
            productName = productName.replaceAll("\\s+", " ").trim();
            
            if (productName.length() < 2) {
                return null;
            }
            
            Integer unitPrice = null;
            Integer totalPrice = null;
            
            if (prices.size() == 1) {
                totalPrice = prices.get(0);
                unitPrice = totalPrice / quantity;
            } else if (prices.size() >= 2) {
                unitPrice = prices.get(0);
                totalPrice = prices.get(prices.size() - 1);
            }
            
            return ReceiptItem.builder()
                    .productName(productName)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .originalText(line)
                    .build();
                    
        } catch (Exception e) {
            log.warn("fallback 파싱 실패: {}, 오류: {}", line, e.getMessage());
            return null;
        }
    }
}