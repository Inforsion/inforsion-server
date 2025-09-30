package com.inforsion.inforsionserver.domain.ocr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrConfirmationRequestDto;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrJobResponseDto;
import com.inforsion.inforsionserver.global.enums.OcrJobStatus;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrProcessingRequestDto;
import com.inforsion.inforsionserver.domain.ocr.dto.ProductMatchingResultDto;
import com.inforsion.inforsionserver.domain.ocr.mongo.entity.OcrRawDataEntity;
import com.inforsion.inforsionserver.domain.ocr.mongo.repository.OcrRawDataRepository;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrJobEntity;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrResultEntity;
import com.inforsion.inforsionserver.domain.ocr.mysql.repository.OcrJobRepository;
import com.inforsion.inforsionserver.domain.ocr.mysql.repository.OcrResultRepository;
import com.inforsion.inforsionserver.domain.ocr.dto.ReceiptItem;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.product.repository.ProductRepository;
import com.inforsion.inforsionserver.domain.recipe.repository.RecipeRepository;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.global.enums.MatchMethod;
import com.inforsion.inforsionserver.global.enums.MatchType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrProcessingService {

    private final OcrRawDataRepository ocrRawDataRepository;
    private final OcrResultRepository ocrResultRepository;
    private final OcrJobRepository ocrJobRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final RecipeRepository recipeRepository;
    private final InventoryUpdateService inventoryUpdateService;
    private final NaverOcrService naverOcrService;
    private final ReceiptAnalysisService receiptAnalysisService;
    private final ObjectMapper objectMapper;

    /**
     * 이미지 파일을 통한 OCR 처리
     */
    @Transactional
    public ProductMatchingResultDto processImageOcr(MultipartFile imageFile, Integer storeId, String documentType) {
        try {
            log.info("이미지 OCR 처리 시작: 파일명={}, 크기={} bytes, 매장ID={}",
                    imageFile.getOriginalFilename(), imageFile.getSize(), storeId);

            // 1. 네이버 OCR API를 통해 이미지에서 텍스트 추출
            OcrProcessingRequestDto ocrRequestDto = naverOcrService.processImageWithNaverOcr(imageFile, storeId, documentType);

            // 2. 추출된 OCR 데이터를 기존 로직으로 처리
            return processOcrData(ocrRequestDto);

        } catch (Exception e) {
            log.error("이미지 OCR 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("이미지 OCR 처리에 실패했습니다: " + e.getMessage(), e);
        }
    }

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
     * 제품 매칭 수행 - ReceiptAnalysisService 사용
     */
    private ProductMatchingResultDto performProductMatching(OcrProcessingRequestDto requestDto, Integer rawDataId) {
        // rawOcrText를 줄 단위로 분할
        List<String> textLines = Arrays.asList(requestDto.getRawOcrText().split("\n"));

        // ReceiptAnalysisService를 사용하여 제품 매칭
        List<ReceiptItem> extractedItems = receiptAnalysisService.extractReceiptItems(textLines, requestDto.getStoreId());

        List<ProductMatchingResultDto.MatchedItemDto> matchedItems = new ArrayList<>();
        List<ProductEntity> storeProducts = productRepository.findByStoreId(requestDto.getStoreId());

        for (ReceiptItem item : extractedItems) {
            // 매칭된 제품 찾기 (부분 문자열 매칭)
            ProductEntity matchedProduct = findBestMatchingProduct(item.getProductName(), storeProducts);

            if (matchedProduct != null) {
                // 정확히 매칭된 제품이 있는 경우
                ProductMatchingResultDto.ProductCandidateDto candidate = ProductMatchingResultDto.ProductCandidateDto.builder()
                        .productId(matchedProduct.getId())
                        .productName(matchedProduct.getName())
                        .price(matchedProduct.getPrice())
                        .similarityScore(1.0) // 완전 매칭
                        .exactMatch(true)
                        .build();

                ProductMatchingResultDto.MatchedItemDto matchedItem = ProductMatchingResultDto.MatchedItemDto.builder()
                        .ocrItemName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getUnitPrice())
                        .totalAmount(item.getTotalPrice())
                        .productCandidates(List.of(candidate))
                        .selectedProductId(matchedProduct.getId())
                        .confirmed(true) // 자동으로 확정
                        .build();

                matchedItems.add(matchedItem);
            }
        }

        log.info("제품 매칭 완료: 총 {}개 아이템 매칭됨", matchedItems.size());

        return ProductMatchingResultDto.builder()
                .rawDataId(rawDataId)
                .matchedItems(matchedItems)
                .build();
    }


    /**
     * 최적의 제품 매칭 찾기 (부분 문자열 + 유사도 기반)
     */
    private ProductEntity findBestMatchingProduct(String ocrText, List<ProductEntity> storeProducts) {
        if (ocrText == null || ocrText.trim().isEmpty()) {
            return null;
        }

        ProductEntity bestMatch = null;
        double bestScore = 0.0;

        for (ProductEntity product : storeProducts) {
            double score = calculateProductMatchScore(ocrText, product.getName());
            log.debug("제품 매칭 점수: '{}' vs '{}' = {}", ocrText, product.getName(), score);

            if (score > bestScore && score >= 0.6) { // 60% 이상 유사도만 허용
                bestScore = score;
                bestMatch = product;
            }
        }

        if (bestMatch != null) {
            log.info("제품 매칭 성공: '{}' → '{}' (점수: {})", ocrText, bestMatch.getName(), bestScore);
        } else {
            log.warn("제품 매칭 실패: '{}' - 유사한 제품을 찾을 수 없음", ocrText);
        }

        return bestMatch;
    }

    /**
     * 제품명 매칭 점수 계산
     */
    private double calculateProductMatchScore(String ocrText, String productName) {
        if (ocrText == null || productName == null) {
            return 0.0;
        }

        // 대소문자 구분 없이 처리
        String normalizedOcr = ocrText.toLowerCase().trim();
        String normalizedProduct = productName.toLowerCase().trim();

        // 1. 완전 일치
        if (normalizedOcr.equals(normalizedProduct)) {
            return 1.0;
        }

        // 2. 부분 문자열 포함 검사
        if (normalizedOcr.contains(normalizedProduct)) {
            return 0.9; // OCR 텍스트가 제품명을 포함하는 경우 (예: "I-T)아메리카노" contains "아메리카노")
        }

        if (normalizedProduct.contains(normalizedOcr)) {
            return 0.8; // 제품명이 OCR 텍스트를 포함하는 경우
        }

        // 3. 키워드 기반 매칭 (공백과 특수문자 제거 후)
        String cleanOcr = normalizedOcr.replaceAll("[^가-힣a-z0-9]", "");
        String cleanProduct = normalizedProduct.replaceAll("[^가-힣a-z0-9]", "");

        if (cleanOcr.contains(cleanProduct) || cleanProduct.contains(cleanOcr)) {
            return 0.7;
        }

        // 4. 레벤슈타인 거리 기반 유사도
        int distance = calculateLevenshteinDistance(cleanOcr, cleanProduct);
        int maxLength = Math.max(cleanOcr.length(), cleanProduct.length());

        if (maxLength == 0) {
            return 0.0;
        }

        double similarity = 1.0 - (double) distance / maxLength;
        return Math.max(0.0, similarity);
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
     * OCR 로우 데이터 상세 조회 (로그 확인용)
     */
    public OcrRawDataEntity getRawDataDetail(Integer rawDataId) {
        OcrRawDataEntity rawData = ocrRawDataRepository.findByRawDataId(rawDataId)
                .orElseThrow(() -> new IllegalArgumentException("원본 OCR 데이터를 찾을 수 없습니다: " + rawDataId));

        log.info("========== OCR 로우 데이터 조회 ==========");
        log.info("Raw Data ID: {}", rawData.getRawDataId());
        log.info("MongoDB ID: {}", rawData.getId());
        log.info("Store ID: {}", rawData.getStoreId());
        log.info("Document Type: {}", rawData.getDocumentType());
        log.info("Supplier Name: {}", rawData.getSupplierName());
        log.info("Document Date: {}", rawData.getDocumentDate());
        log.info("Created At: {}", rawData.getCreatedAt());
        log.info("Raw OCR Text Length: {} characters", rawData.getRawOcrText() != null ? rawData.getRawOcrText().length() : 0);
        log.info("========== Raw OCR Text ==========");
        log.info("\n{}", rawData.getRawOcrText());
        log.info("========== Parsed Items JSON ==========");
        log.info("{}", rawData.getParsedItem());
        log.info("==========================================");

        return rawData;
    }

    /**
     * 1단계 결과를 사용자 수정용으로 조회 (1.5단계)
     */
    public ProductMatchingResultDto getOcrPreview(Integer rawDataId) {
        try {
            // MongoDB에서 원본 데이터 조회
            OcrRawDataEntity rawData = ocrRawDataRepository.findByRawDataId(rawDataId)
                    .orElseThrow(() -> new IllegalArgumentException("원본 OCR 데이터를 찾을 수 없습니다: " + rawDataId));

            // 원본 OCR 텍스트를 다시 파싱해서 매칭 결과 생성
            OcrProcessingRequestDto requestDto = OcrProcessingRequestDto.builder()
                    .storeId(rawData.getStoreId())
                    .documentType(rawData.getDocumentType())
                    .rawOcrText(rawData.getRawOcrText())
                    .parsedItems(new ArrayList<>()) // 빈 리스트
                    .supplierName(rawData.getSupplierName())
                    .documentDate(rawData.getDocumentDate())
                    .build();

            // 제품 매칭 다시 수행
            ProductMatchingResultDto result = performProductMatching(requestDto, rawDataId);

            log.info("OCR 미리보기 생성 완료: rawDataId={}, 매칭된 아이템 수={}",
                    rawDataId, result.getMatchedItems().size());

            return result;

        } catch (Exception e) {
            log.error("OCR 미리보기 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("OCR 미리보기 생성에 실패했습니다.", e);
        }
    }

    /**
     * 특정 매장의 OCR 처리 이력 조회
     */
    public List<OcrResultEntity> getOcrHistory(Integer storeId) {
        return ocrResultRepository.findByStoreIdOrderByCreatedAtDesc(storeId);
    }

    // ===== 비동기 처리 메서드들 =====

    /**
     * 비동기 이미지 OCR 처리 시작
     */
    public OcrJobResponseDto startAsyncImageOcr(MultipartFile imageFile, Integer storeId, String documentType) {
        try {
            // 1. Job ID 생성 및 초기 Job 엔티티 생성
            String jobUuid = UUID.randomUUID().toString();
            StoreEntity store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다: " + storeId));

            OcrJobEntity jobEntity = OcrJobEntity.builder()
                    .jobUuid(jobUuid)
                    .store(store)
                    .documentType(documentType)
                    .originalFilename(imageFile.getOriginalFilename())
                    .fileSize(imageFile.getSize())
                    .status(OcrJobStatus.PENDING)
                    .build();

            ocrJobRepository.save(jobEntity);

            // 2. 비동기 처리 시작
            processImageOcrAsync(jobUuid, imageFile, storeId, documentType);

            // 3. 즉시 응답 반환
            return OcrJobResponseDto.fromPending(jobUuid);

        } catch (Exception e) {
            log.error("비동기 OCR 작업 시작 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("비동기 OCR 작업 시작에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 비동기 OCR 데이터 처리 시작
     */
    public OcrJobResponseDto startAsyncOcrData(OcrProcessingRequestDto requestDto) {
        try {
            // 1. Job ID 생성 및 초기 Job 엔티티 생성
            String jobUuid = UUID.randomUUID().toString();
            StoreEntity store = storeRepository.findById(requestDto.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다: " + requestDto.getStoreId()));

            OcrJobEntity jobEntity = OcrJobEntity.builder()
                    .jobUuid(jobUuid)
                    .store(store)
                    .documentType(requestDto.getDocumentType().name())
                    .status(OcrJobStatus.PENDING)
                    .build();

            ocrJobRepository.save(jobEntity);

            // 2. 비동기 처리 시작
            processOcrDataAsync(jobUuid, requestDto);

            // 3. 즉시 응답 반환
            return OcrJobResponseDto.fromPending(jobUuid);

        } catch (Exception e) {
            log.error("비동기 OCR 데이터 처리 시작 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("비동기 OCR 데이터 처리 시작에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 비동기 이미지 OCR 처리 실행
     */
    @Async("ocrTaskExecutor")
    public void processImageOcrAsync(String jobUuid, MultipartFile imageFile, Integer storeId, String documentType) {
        OcrJobEntity jobEntity = null;
        try {
            // Job 상태를 PROCESSING으로 변경
            jobEntity = ocrJobRepository.findByJobUuid(jobUuid)
                    .orElseThrow(() -> new IllegalArgumentException("Job을 찾을 수 없습니다: " + jobUuid));

            jobEntity.updateStatus(OcrJobStatus.PROCESSING);
            ocrJobRepository.save(jobEntity);

            log.info("비동기 이미지 OCR 처리 시작: jobUuid={}, 파일명={}", jobUuid, imageFile.getOriginalFilename());

            // 진행률 10% 업데이트
            jobEntity.updateProgress(10);
            ocrJobRepository.save(jobEntity);

            // 1. 네이버 OCR API 호출
            OcrProcessingRequestDto ocrRequestDto = naverOcrService.processImageWithNaverOcr(imageFile, storeId, documentType);

            // 진행률 50% 업데이트
            jobEntity.updateProgress(50);
            ocrJobRepository.save(jobEntity);

            // 2. OCR 데이터 처리
            ProductMatchingResultDto result = processOcrData(ocrRequestDto);

            // 진행률 90% 업데이트
            jobEntity.updateProgress(90);
            ocrJobRepository.save(jobEntity);

            // 3. 결과 저장 및 완료 처리
            jobEntity.setRawDataId(result.getRawDataId());
            jobEntity.setMatchingResultJson(objectMapper.writeValueAsString(result));
            jobEntity.updateStatus(OcrJobStatus.COMPLETED);
            ocrJobRepository.save(jobEntity);

            log.info("비동기 이미지 OCR 처리 완료: jobUuid={}, rawDataId={}", jobUuid, result.getRawDataId());

        } catch (Exception e) {
            log.error("비동기 이미지 OCR 처리 중 오류 발생: jobUuid={}, 오류={}", jobUuid, e.getMessage(), e);
            if (jobEntity != null) {
                jobEntity.setError("OCR 처리 중 오류 발생: " + e.getMessage());
                ocrJobRepository.save(jobEntity);
            }
        }
    }

    /**
     * 비동기 OCR 데이터 처리 실행
     */
    @Async("ocrTaskExecutor")
    public void processOcrDataAsync(String jobUuid, OcrProcessingRequestDto requestDto) {
        OcrJobEntity jobEntity = null;
        try {
            // Job 상태를 PROCESSING으로 변경
            jobEntity = ocrJobRepository.findByJobUuid(jobUuid)
                    .orElseThrow(() -> new IllegalArgumentException("Job을 찾을 수 없습니다: " + jobUuid));

            jobEntity.updateStatus(OcrJobStatus.PROCESSING);
            ocrJobRepository.save(jobEntity);

            log.info("비동기 OCR 데이터 처리 시작: jobUuid={}, 매장ID={}", jobUuid, requestDto.getStoreId());

            // 진행률 20% 업데이트
            jobEntity.updateProgress(20);
            ocrJobRepository.save(jobEntity);

            // OCR 데이터 처리
            ProductMatchingResultDto result = processOcrData(requestDto);

            // 진행률 90% 업데이트
            jobEntity.updateProgress(90);
            ocrJobRepository.save(jobEntity);

            // 결과 저장 및 완료 처리
            jobEntity.setRawDataId(result.getRawDataId());
            jobEntity.setMatchingResultJson(objectMapper.writeValueAsString(result));
            jobEntity.updateStatus(OcrJobStatus.COMPLETED);
            ocrJobRepository.save(jobEntity);

            log.info("비동기 OCR 데이터 처리 완료: jobUuid={}, rawDataId={}", jobUuid, result.getRawDataId());

        } catch (Exception e) {
            log.error("비동기 OCR 데이터 처리 중 오류 발생: jobUuid={}, 오류={}", jobUuid, e.getMessage(), e);
            if (jobEntity != null) {
                jobEntity.setError("OCR 데이터 처리 중 오류 발생: " + e.getMessage());
                ocrJobRepository.save(jobEntity);
            }
        }
    }

    /**
     * OCR Job 상태 조회
     */
    public OcrJobResponseDto getJobStatus(String jobUuid) {
        OcrJobEntity jobEntity = ocrJobRepository.findByJobUuid(jobUuid)
                .orElseThrow(() -> new IllegalArgumentException("Job을 찾을 수 없습니다: " + jobUuid));

        try {
            switch (jobEntity.getStatus()) {
                case PENDING:
                    return OcrJobResponseDto.builder()
                            .jobId(jobUuid)
                            .status(OcrJobStatus.PENDING)
                            .progressPercentage(0)
                            .message("OCR 작업이 대기 중입니다.")
                            .createdAt(jobEntity.getCreatedAt())
                            .build();

                case PROCESSING:
                    return OcrJobResponseDto.builder()
                            .jobId(jobUuid)
                            .status(OcrJobStatus.PROCESSING)
                            .progressPercentage(jobEntity.getProgressPercentage())
                            .message("OCR 작업을 처리 중입니다.")
                            .createdAt(jobEntity.getCreatedAt())
                            .startedAt(jobEntity.getStartedAt())
                            .build();

                case COMPLETED:
                    ProductMatchingResultDto result = null;
                    if (jobEntity.getMatchingResultJson() != null) {
                        result = objectMapper.readValue(jobEntity.getMatchingResultJson(), ProductMatchingResultDto.class);
                    }
                    return OcrJobResponseDto.builder()
                            .jobId(jobUuid)
                            .status(OcrJobStatus.COMPLETED)
                            .progressPercentage(100)
                            .message("OCR 작업이 완료되었습니다.")
                            .createdAt(jobEntity.getCreatedAt())
                            .startedAt(jobEntity.getStartedAt())
                            .completedAt(jobEntity.getCompletedAt())
                            .result(result)
                            .build();

                case FAILED:
                    return OcrJobResponseDto.builder()
                            .jobId(jobUuid)
                            .status(OcrJobStatus.FAILED)
                            .progressPercentage(0)
                            .message("OCR 작업이 실패했습니다.")
                            .createdAt(jobEntity.getCreatedAt())
                            .startedAt(jobEntity.getStartedAt())
                            .completedAt(jobEntity.getCompletedAt())
                            .errorMessage(jobEntity.getErrorMessage())
                            .build();

                default:
                    throw new IllegalStateException("알 수 없는 Job 상태: " + jobEntity.getStatus());
            }
        } catch (JsonProcessingException e) {
            log.error("Job 결과 JSON 파싱 오류: jobUuid={}, 오류={}", jobUuid, e.getMessage(), e);
            throw new RuntimeException("Job 결과를 조회하는 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 매장의 모든 OCR Job 조회
     */
    public List<OcrJobResponseDto> getStoreJobs(Integer storeId) {
        List<OcrJobEntity> jobs = ocrJobRepository.findByStoreIdOrderByCreatedAtDesc(storeId);
        return jobs.stream()
                .map(job -> {
                    try {
                        return getJobStatus(job.getJobUuid());
                    } catch (Exception e) {
                        log.warn("Job 상태 조회 중 오류: jobUuid={}, 오류={}", job.getJobUuid(), e.getMessage());
                        return OcrJobResponseDto.fromFailed(job.getJobUuid(), "상태 조회 실패");
                    }
                })
                .collect(Collectors.toList());
    }
}