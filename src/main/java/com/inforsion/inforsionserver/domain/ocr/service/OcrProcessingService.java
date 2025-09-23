package com.inforsion.inforsionserver.domain.ocr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrConfirmationRequestDto;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrProcessingRequestDto;
import com.inforsion.inforsionserver.domain.ocr.dto.ProductMatchingResultDto;
import com.inforsion.inforsionserver.domain.ocr.mongo.entity.OcrRawDataEntity;
import com.inforsion.inforsionserver.domain.ocr.mongo.repository.OcrRawDataRepository;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrResultEntity;
import com.inforsion.inforsionserver.domain.ocr.mysql.repository.OcrResultRepository;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.product.repository.ProductRepository;
import com.inforsion.inforsionserver.domain.recipe.entity.RecipeEntity;
import com.inforsion.inforsionserver.domain.recipe.repository.RecipeRepository;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.global.enums.MatchMethod;
import com.inforsion.inforsionserver.global.enums.MatchType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrProcessingService {

    private final OcrRawDataRepository ocrRawDataRepository;
    private final OcrResultRepository ocrResultRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final RecipeRepository recipeRepository;
    private final InventoryUpdateService inventoryUpdateService;
    private final ObjectMapper objectMapper;

    /**
     * 1단계: OCR 원본 데이터를 MongoDB에 저장
     */
    @Transactional
    public ProductMatchingResultDto processOcrData(OcrProcessingRequestDto requestDto) {
        try {
            // 1. MongoDB에 원본 데이터 저장
            Integer rawDataId = saveRawData(requestDto);
            
            // 2. 제품 매칭 수행
            ProductMatchingResultDto matchingResult = performProductMatching(requestDto, rawDataId);
            
            return matchingResult;
            
        } catch (Exception e) {
            log.error("OCR 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("OCR 데이터 처리에 실패했습니다.", e);
        }
    }

    /**
     * MongoDB에 원본 데이터 저장
     */
    private Integer saveRawData(OcrProcessingRequestDto requestDto) throws JsonProcessingException {
        // 파싱된 아이템들을 JSON으로 변환
        String parsedItemJson = objectMapper.writeValueAsString(requestDto.getParsedItems());
        
        OcrRawDataEntity rawDataEntity = OcrRawDataEntity.builder()
                .storeId(requestDto.getStoreId())
                .documentType(requestDto.getDocumentType())
                .rawOcrText(requestDto.getRawOcrText())
                .parsedItem(parsedItemJson)
                .supplierName(requestDto.getSupplierName())
                .documentDate(requestDto.getDocumentDate())
                .build();
                
        // MongoDB에 저장하고 자동 증가된 rawDataId 설정
        OcrRawDataEntity savedEntity = ocrRawDataRepository.save(rawDataEntity);
        
        // MongoDB의 ObjectId를 기반으로 rawDataId 생성 (간단한 해시 또는 순번 사용)
        Integer rawDataId = generateRawDataId(savedEntity.getId());
        savedEntity.setRawDataId(rawDataId);
        ocrRawDataRepository.save(savedEntity);
        
        return rawDataId;
    }

    /**
     * 제품 매칭 수행
     */
    private ProductMatchingResultDto performProductMatching(OcrProcessingRequestDto requestDto, Integer rawDataId) {
        List<ProductEntity> storeProducts = productRepository.findByStoreId(requestDto.getStoreId());
        List<ProductMatchingResultDto.MatchedItemDto> matchedItems = new ArrayList<>();
        
        for (OcrProcessingRequestDto.OcrItemDto ocrItem : requestDto.getParsedItems()) {
            List<ProductMatchingResultDto.ProductCandidateDto> candidates = findProductCandidates(ocrItem, storeProducts);
            
            ProductMatchingResultDto.MatchedItemDto matchedItem = ProductMatchingResultDto.MatchedItemDto.builder()
                    .ocrItemName(ocrItem.getItemName())
                    .quantity(ocrItem.getQuantity())
                    .price(ocrItem.getPrice())
                    .totalAmount(ocrItem.getTotalAmount())
                    .productCandidates(candidates)
                    .confirmed(false)
                    .build();
                    
            // 정확히 일치하는 제품이 있으면 자동 선택
            candidates.stream()
                    .filter(ProductMatchingResultDto.ProductCandidateDto::getExactMatch)
                    .findFirst()
                    .ifPresent(exactMatch -> {
                        matchedItem.setSelectedProductId(exactMatch.getProductId());
                        matchedItem.setConfirmed(true);
                    });
                    
            matchedItems.add(matchedItem);
        }
        
        return ProductMatchingResultDto.builder()
                .rawDataId(rawDataId)
                .matchedItems(matchedItems)
                .build();
    }

    /**
     * 제품 후보 찾기 (유사도 기반)
     */
    private List<ProductMatchingResultDto.ProductCandidateDto> findProductCandidates(
            OcrProcessingRequestDto.OcrItemDto ocrItem, List<ProductEntity> storeProducts) {
        
        return storeProducts.stream()
                .map(product -> {
                    double similarity = calculateSimilarity(ocrItem.getItemName(), product.getName());
                    boolean exactMatch = similarity >= 0.9; // 90% 이상 유사하면 정확한 매치로 간주
                    
                    return ProductMatchingResultDto.ProductCandidateDto.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .price(product.getPrice())
                            .similarityScore(similarity)
                            .exactMatch(exactMatch)
                            .build();
                })
                .filter(candidate -> candidate.getSimilarityScore() >= 0.5) // 50% 이상 유사한 것만 후보로
                .sorted((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore())) // 유사도 높은 순으로 정렬
                .limit(5) // 상위 5개만
                .collect(Collectors.toList());
    }

    /**
     * 문자열 유사도 계산 (레벤슈타인 거리 기반)
     */
    private double calculateSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) return 0.0;
        
        str1 = str1.toLowerCase().trim();
        str2 = str2.toLowerCase().trim();
        
        if (str1.equals(str2)) return 1.0;
        
        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) return 1.0;
        
        int levenshteinDistance = calculateLevenshteinDistance(str1, str2);
        return 1.0 - (double) levenshteinDistance / maxLength;
    }

    /**
     * 레벤슈타인 거리 계산
     */
    private int calculateLevenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        
        for (int i = 0; i <= str1.length(); i++) {
            for (int j = 0; j <= str2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(Math.min(
                            dp[i - 1][j] + 1,      // deletion
                            dp[i][j - 1] + 1),    // insertion
                            dp[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1)); // substitution
                }
            }
        }
        
        return dp[str1.length()][str2.length()];
    }

    /**
     * MongoDB ObjectId를 기반으로 rawDataId 생성
     */
    private Integer generateRawDataId(String mongoId) {
        // 간단한 해시 기반 ID 생성 (실제로는 더 정교한 방법 사용 가능)
        return Math.abs(mongoId.hashCode() % 1000000);
    }

    /**
     * 3단계: 사용자 확인/수정 후 MySQL에 저장하고 재고 업데이트
     */
    @Transactional
    public void confirmOcrResults(OcrConfirmationRequestDto confirmationDto) {
        try {
            // MongoDB에서 원본 데이터 조회
            OcrRawDataEntity rawData = ocrRawDataRepository.findByRawDataId(confirmationDto.getRawDataId())
                    .orElseThrow(() -> new IllegalArgumentException("원본 OCR 데이터를 찾을 수 없습니다: " + confirmationDto.getRawDataId()));
            
            StoreEntity store = storeRepository.findById(rawData.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다: " + rawData.getStoreId()));
            
            // 각 확정된 아이템을 MySQL에 저장하고 재고 업데이트
            for (OcrConfirmationRequestDto.ConfirmedItemDto confirmedItem : confirmationDto.getConfirmedItems()) {
                saveOcrResultToMySQL(rawData, store, confirmedItem);
            }
            
            log.info("OCR 결과 확정 완료: rawDataId={}, 아이템 수={}", 
                    confirmationDto.getRawDataId(), confirmationDto.getConfirmedItems().size());
                    
        } catch (Exception e) {
            log.error("OCR 결과 확정 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("OCR 결과 확정에 실패했습니다.", e);
        }
    }

    /**
     * 확정된 OCR 결과를 MySQL에 저장
     */
    private void saveOcrResultToMySQL(OcrRawDataEntity rawData, StoreEntity store, 
                                     OcrConfirmationRequestDto.ConfirmedItemDto confirmedItem) {
        
        // 선택된 제품 정보 조회
        ProductEntity selectedProduct = productRepository.findById(confirmedItem.getSelectedProductId())
                .orElseThrow(() -> new IllegalArgumentException("선택된 제품을 찾을 수 없습니다: " + confirmedItem.getSelectedProductId()));
        
        // 매치 방법 결정 (사용자가 수정했는지 여부에 따라)
        MatchMethod matchMethod = (confirmedItem.getCorrectedItemName() != null && 
                                 !confirmedItem.getCorrectedItemName().equals(confirmedItem.getOcrItemName())) 
                                 ? MatchMethod.MANUAL : MatchMethod.AUTO;
        
        // 매치 타입 결정 (제품 매칭이므로 Menu)
        MatchType matchType = MatchType.MENU;
        
        // OCR 결과 엔티티 생성
        OcrResultEntity ocrResult = OcrResultEntity.builder()
                .store(store)
                .rawDataId(rawData.getRawDataId())
                .ocrItemName(confirmedItem.getCorrectedItemName() != null ? 
                           confirmedItem.getCorrectedItemName() : confirmedItem.getOcrItemName())
                .quantity(confirmedItem.getQuantity())
                .price(confirmedItem.getPrice())
                .matchType(matchType)
                .targetId(selectedProduct.getId())
                .totalAmount(BigDecimal.valueOf(confirmedItem.getTotalAmount()))
                .matchMethod(matchMethod)
                .build();
        
        // MySQL에 저장
        OcrResultEntity savedResult = ocrResultRepository.save(ocrResult);
        
        // 재고 업데이트
        inventoryUpdateService.updateInventoryFromOcr(savedResult);
        
        log.info("OCR 결과 MySQL 저장 완료: {}, 제품: {}, 수량: {}", 
                savedResult.getOcrId(), selectedProduct.getName(), confirmedItem.getQuantity());
    }

    /**
     * MongoDB에서 OCR 원본 데이터 조회
     */
    public OcrRawDataEntity getRawData(Integer rawDataId) {
        return ocrRawDataRepository.findByRawDataId(rawDataId)
                .orElseThrow(() -> new IllegalArgumentException("원본 OCR 데이터를 찾을 수 없습니다: " + rawDataId));
    }

    /**
     * 특정 매장의 OCR 처리 이력 조회
     */
    public List<OcrResultEntity> getOcrHistory(Integer storeId) {
        return ocrResultRepository.findByStoreIdOrderByCreatedAtDesc(storeId);
    }
}