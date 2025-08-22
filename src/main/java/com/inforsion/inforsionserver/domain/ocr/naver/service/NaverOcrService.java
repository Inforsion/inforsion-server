package com.inforsion.inforsionserver.domain.ocr.naver.service;

import com.inforsion.inforsionserver.domain.ocr.naver.client.NaverOcrClient;
import com.inforsion.inforsionserver.domain.ocr.naver.dto.response.OcrResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverOcrService {

    private final NaverOcrClient naverOcrClient;

    public OcrResponse processImage(MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        
        log.info("네이버 OCR 처리 시작 - 파일명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize());
        
        try {
            // MultipartFile을 byte array로 변환
            byte[] imageBytes = file.getBytes();
            
            // NaverOcrClient를 통해 OCR 처리
            String extractedText = naverOcrClient.parseText(imageBytes, file.getOriginalFilename());
            
            // 텍스트를 줄별로 분리
            List<String> extractedLines = Arrays.stream(extractedText.split(System.lineSeparator()))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            log.info("네이버 OCR 처리 완료 - 소요시간: {}ms, 추출된 줄 수: {}", processingTime, extractedLines.size());
            
            // 콘솔에 OCR 결과 출력
            printOcrResult(file, extractedText, extractedLines, processingTime);
            
            return OcrResponse.builder()
                    .originalFileName(file.getOriginalFilename())
                    .extractedText(extractedText)
                    .extractedLines(extractedLines)
                    .processingTimeMs(processingTime)
                    .fileSizeBytes(file.getSize())
                    .ocrEngine("Naver OCR WebClient")
                    .confidence(1.0) // WebClient 버전에서는 신뢰도 정보를 별도로 처리
                    .build();
                    
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("네이버 OCR 처리 실패 - 파일명: {}, 소요시간: {}ms, 오류: {}", 
                    file.getOriginalFilename(), processingTime, e.getMessage());
            throw new RuntimeException("네이버 OCR 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    private void printOcrResult(MultipartFile file, String extractedText, List<String> extractedLines, 
                               long processingTime) {
        System.out.println("=== 네이버 OCR 처리 결과 (WebClient) ===");
        System.out.println("파일명: " + file.getOriginalFilename());
        System.out.println("파일 크기: " + file.getSize() + " bytes");
        System.out.println("처리 시간: " + processingTime + "ms");
        System.out.println("--- 추출된 전체 텍스트 ---");
        System.out.println(extractedText);
        System.out.println("--- 줄별 텍스트 ---");
        for (int i = 0; i < extractedLines.size(); i++) {
            System.out.println((i + 1) + ": " + extractedLines.get(i));
        }
        System.out.println("==============================");
    }
}