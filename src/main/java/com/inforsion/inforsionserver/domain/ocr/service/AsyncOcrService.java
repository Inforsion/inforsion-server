package com.inforsion.inforsionserver.domain.ocr.service;

import com.inforsion.inforsionserver.domain.ocr.dto.OcrJobResult;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrJobStatus;
import com.inforsion.inforsionserver.domain.ocr.dto.ReceiptItem;
import com.inforsion.inforsionserver.domain.ocr.mongo.entity.OcrResultEntity;
import com.inforsion.inforsionserver.domain.ocr.mongo.repository.OcrResultRepository;
import com.inforsion.inforsionserver.domain.ocr.naver.client.NaverOcrClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncOcrService {

    private final NaverOcrClient naverOcrClient;
    private final OcrResultRepository ocrResultRepository;
    private final ReceiptAnalysisService receiptAnalysisService;
    
    // 메모리 기반 작업 상태 캐시 (운영환경에서는 Redis 권장)
    private final Map<String, OcrJobResult> jobCache = new ConcurrentHashMap<>();

    /**
     * 비동기 OCR 작업 시작 (Job ID 미리 생성)
     */
    @Async("ocrTaskExecutor")
    public CompletableFuture<OcrJobResult> processOcrAsyncWithJobId(MultipartFile file, String jobId) {
        String originalFileName = file.getOriginalFilename();
        long startTime = System.currentTimeMillis();

        log.info("비동기 OCR 작업 시작 - JobId: {}, 파일명: {}, 크기: {} bytes", 
                jobId, originalFileName, file.getSize());

        try {
            // 1. 대기 상태로 초기화
            OcrJobResult pendingResult = OcrJobResult.pending(jobId, originalFileName);
            jobCache.put(jobId, pendingResult);

            // 2. 처리 중 상태로 변경
            OcrJobResult processingResult = OcrJobResult.processing(jobId, originalFileName);
            jobCache.put(jobId, processingResult);

            // 3. 네이버 OCR API 호출 (병렬 처리 가능한 부분)
            CompletableFuture<String> ocrFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return naverOcrClient.parseText(file.getBytes(), file.getOriginalFilename());
                } catch (Exception e) {
                    log.error("네이버 OCR API 호출 실패 - JobId: {}, 오류: {}", jobId, e.getMessage());
                    throw new RuntimeException("OCR API 호출 실패", e);
                }
            });

            // 4. OCR 결과 처리 및 영수증 분석
            String extractedText = ocrFuture.get();
            
            // OCR 결과에서 텍스트를 라인별로 분할
            List<String> extractedLines = List.of(extractedText.split("\n"));

            // 영수증 항목 분석 (병렬 처리)
            CompletableFuture<List<ReceiptItem>> receiptFuture = CompletableFuture.supplyAsync(() -> 
                    receiptAnalysisService.extractReceiptItems(extractedLines)
            );

            List<ReceiptItem> receiptItems = receiptFuture.get();
            long processingTime = System.currentTimeMillis() - startTime;

            // 5. MongoDB에 결과 저장 (비동기)
            CompletableFuture<OcrResultEntity> saveFuture = CompletableFuture.supplyAsync(() -> {
                OcrResultEntity ocrResult = OcrResultEntity.builder()
                        .originalFileName(originalFileName)
                        .extractedText(extractedText)
                        .extractedLines(extractedLines)
                        .receiptItems(receiptItems)
                        .processingTimeMs(processingTime)
                        .fileSizeBytes(file.getSize())
                        .ocrEngine("Naver OCR Async")
                        .confidence(1.0)
                        .build();

                return ocrResultRepository.save(ocrResult);
            });

            // 6. 완료 상태로 업데이트
            OcrResultEntity savedResult = saveFuture.get();
            OcrJobResult completedResult = OcrJobResult.completed(
                    jobId, originalFileName, extractedText, extractedLines, receiptItems,
                    "Naver OCR Async", 1.0, processingTime, file.getSize()
            );
            
            jobCache.put(jobId, completedResult);

            log.info("비동기 OCR 작업 완료 - JobId: {}, 처리시간: {}ms, 상품수: {}", 
                    jobId, processingTime, receiptItems.size());

            return CompletableFuture.completedFuture(completedResult);

        } catch (Exception e) {
            // 7. 실패 상태로 업데이트
            long processingTime = System.currentTimeMillis() - startTime;
            String errorMessage = "OCR 처리 실패: " + e.getMessage();
            
            OcrJobResult failedResult = OcrJobResult.failed(jobId, originalFileName, errorMessage);
            jobCache.put(jobId, failedResult);

            log.error("비동기 OCR 작업 실패 - JobId: {}, 처리시간: {}ms, 오류: {}", 
                    jobId, processingTime, errorMessage, e);

            return CompletableFuture.completedFuture(failedResult);
        }
    }

    /**
     * 작업 상태 조회
     */
    @Cacheable(value = "ocrJobStatus", key = "#jobId")
    public OcrJobResult getJobStatus(String jobId) {
        OcrJobResult result = jobCache.get(jobId);
        if (result == null) {
            log.warn("존재하지 않는 작업 ID 조회 시도: {}", jobId);
            return OcrJobResult.failed(jobId, "Unknown", "작업을 찾을 수 없습니다.");
        }
        return result;
    }

    /**
     * 비동기 OCR 작업 시작 (기본 메서드, 하위 호환성)
     */
    @Async("ocrTaskExecutor")
    public CompletableFuture<OcrJobResult> processOcrAsync(MultipartFile file) {
        String jobId = UUID.randomUUID().toString();
        return processOcrAsyncWithJobId(file, jobId);
    }

    /**
     * 배치 OCR 처리 (여러 파일 동시 처리)
     */
    @Async("ocrTaskExecutor")
    public CompletableFuture<List<OcrJobResult>> processBatchAsync(List<MultipartFile> files) {
        log.info("배치 OCR 작업 시작 - 파일 수: {}", files.size());

        List<CompletableFuture<OcrJobResult>> futures = files.stream()
                .map(this::processOcrAsync)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }
}