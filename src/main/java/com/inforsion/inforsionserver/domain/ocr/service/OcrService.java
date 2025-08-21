package com.inforsion.inforsionserver.domain.ocr.service;

import com.inforsion.inforsionserver.domain.ocr.dto.response.OcrResponse;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OcrService {

    private final ITesseract tesseract;

    public OcrService() {
        this.tesseract = new Tesseract();
        
        try {
            // 환경별 tessdata 경로 설정
            String tessdataPath = findTessdataPath();
            if (tessdataPath != null) {
                tesseract.setDatapath(tessdataPath);
                log.info("Tesseract 데이터 경로 설정: {}", tessdataPath);
            }
            
            // 기본 언어를 한국어+영어로 설정
            tesseract.setLanguage("kor+eng");
            
            // OCR 엔진 모드 설정 (1: Neural nets LSTM engine only)
            tesseract.setOcrEngineMode(1);
            
            // 페이지 세그멘테이션 모드 (3: Fully automatic page segmentation, but no OSD)
            tesseract.setPageSegMode(3);
            
            log.info("Tesseract OCR 초기화 완료 - 언어: kor+eng, 엔진: LSTM");
        } catch (Exception e) {
            log.warn("Tesseract 한국어 설정 실패, 영어로 대체. 오류: {}", e.getMessage());
            try {
                // 영어로 fallback
                String tessdataPath = findTessdataPath();
                if (tessdataPath != null) {
                    tesseract.setDatapath(tessdataPath);
                }
                tesseract.setLanguage("eng");
                tesseract.setOcrEngineMode(1);
                tesseract.setPageSegMode(3);
                log.info("Tesseract OCR 영어 모드로 초기화 완료");
            } catch (Exception ex) {
                log.error("Tesseract 초기화 완전 실패: {}", ex.getMessage());
                throw new RuntimeException("Tesseract OCR 초기화 실패", ex);
            }
        }
    }
    
    /**
     * 환경별 tessdata 경로 찾기
     */
    private String findTessdataPath() {
        // 가능한 tessdata 경로들 (우선순위 순)
        String[] possiblePaths = {
            "/usr/share/tessdata",                          // Docker 환경 (일반적)
            "/usr/share/tesseract-ocr/4.00/tessdata",      // Ubuntu/Debian
            "/usr/share/tesseract-ocr/5.00/tessdata",      // 최신 버전
            "/opt/homebrew/share/tessdata",                 // macOS Homebrew
            "/usr/local/share/tessdata",                    // 일반적인 Unix
            "C:/Program Files/Tesseract-OCR/tessdata"      // Windows
        };
        
        for (String path : possiblePaths) {
            java.io.File dir = new java.io.File(path);
            if (dir.exists() && dir.isDirectory()) {
                log.info("tessdata 경로 발견: {}", path);
                return path;
            }
        }
        
        log.warn("tessdata 경로를 찾을 수 없습니다. 시스템 기본값 사용");
        return null;
    }

    public OcrResponse processImage(MultipartFile file) throws IOException, TesseractException {
        long startTime = System.currentTimeMillis();
        
        log.info("OCR 처리 시작 - 파일명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize());
        
        // MultipartFile을 BufferedImage로 변환
        BufferedImage image = ImageIO.read(file.getInputStream());
        
        if (image == null) {
            throw new IllegalArgumentException("이미지 파일을 읽을 수 없습니다: " + file.getOriginalFilename());
        }
        
        // OCR 실행
        String extractedText = tesseract.doOCR(image);
        
        // 텍스트를 줄별로 분리 (빈 줄 제거)
        List<String> extractedLines = Arrays.stream(extractedText.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        log.info("OCR 처리 완료 - 소요시간: {}ms, 추출된 줄 수: {}", processingTime, extractedLines.size());
        
        // 콘솔에 OCR 결과 출력 (테스트용)
        System.out.println("=== OCR 처리 결과 ===");
        System.out.println("파일명: " + file.getOriginalFilename());
        System.out.println("파일 크기: " + file.getSize() + " bytes");
        System.out.println("이미지 크기: " + image.getWidth() + "x" + image.getHeight() + " pixels");
        System.out.println("처리 시간: " + processingTime + "ms");
        System.out.println("--- 추출된 텍스트 ---");
        System.out.println(extractedText);
        System.out.println("--- 줄별 텍스트 ---");
        for (int i = 0; i < extractedLines.size(); i++) {
            System.out.println((i + 1) + ": " + extractedLines.get(i));
        }
        System.out.println("==================");
        
        return OcrResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .extractedText(extractedText)
                .extractedLines(extractedLines)
                .processingTimeMs(processingTime)
                .fileSizeBytes(file.getSize())
                .imageWidth(image.getWidth())
                .imageHeight(image.getHeight())
                .build();
    }
    
    /**
     * Tesseract 설정 정보 확인
     */
    public void printTesseractInfo() {
        System.out.println("=== Tesseract 설정 정보 ===");
        System.out.println("Tesseract OCR이 초기화되었습니다.");
        System.out.println("언어: kor+eng (한국어+영어)");
        System.out.println("========================");
    }
}