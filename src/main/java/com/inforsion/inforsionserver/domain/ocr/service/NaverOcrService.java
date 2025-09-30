package com.inforsion.inforsionserver.domain.ocr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrProcessingRequestDto;
import com.inforsion.inforsionserver.global.config.NaverOcrConfig;
import com.inforsion.inforsionserver.global.enums.DocumentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverOcrService {

    private final NaverOcrConfig naverOcrConfig;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /**
     * 네이버 OCR API를 통해 이미지에서 텍스트 추출
     */
    public OcrProcessingRequestDto processImageWithNaverOcr(MultipartFile imageFile, Integer storeId, String documentType) {
        try {
            log.info("네이버 OCR API 호출 시작: 파일명={}, 크기={} bytes",
                    imageFile.getOriginalFilename(), imageFile.getSize());

            // 1. 네이버 OCR API 호출
            String ocrResponseJson = callNaverOcrApi(imageFile);

            // 2. OCR 응답 파싱
            String rawOcrText = extractRawTextFromResponse(ocrResponseJson);
            List<String> textLines = extractTextLinesFromResponse(ocrResponseJson);

            log.info("추출된 텍스트 라인들: {}", textLines);

            // 3. 복잡한 파싱 로직 제거 - 글자 인식 결과만 사용
            List<OcrProcessingRequestDto.OcrItemDto> parsedItems = new ArrayList<>(); // 빈 리스트
            log.info("OCR 텍스트만 추출 완료. 복잡한 파싱 로직은 비활성화됨");

            // 4. 문서 타입 변환
            DocumentType docType = "RECEIPT".equalsIgnoreCase(documentType) ?
                    DocumentType.SALES_RECEIPT : DocumentType.SUPPLY_INVOICE;

            // 5. OcrProcessingRequestDto 생성 (로우 텍스트만)
            return OcrProcessingRequestDto.builder()
                    .storeId(storeId)
                    .documentType(docType)
                    .rawOcrText(rawOcrText)
                    .parsedItems(parsedItems) // 빈 리스트
                    .supplierName("") // 공급업체 추출 비활성화
                    .documentDate(java.time.LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("네이버 OCR 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("OCR 처리에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 네이버 OCR API 호출
     */
    private String callNaverOcrApi(MultipartFile imageFile) throws IOException {
        // API URL 및 Secret Key 확인
        if (naverOcrConfig.getApiUrl() == null || naverOcrConfig.getApiUrl().isEmpty()) {
            throw new IllegalStateException("네이버 OCR API URL이 설정되지 않았습니다.");
        }
        if (naverOcrConfig.getSecretKey() == null || naverOcrConfig.getSecretKey().isEmpty()) {
            throw new IllegalStateException("네이버 OCR Secret Key가 설정되지 않았습니다.");
        }

        // OCR 옵션 생성
        Map<String, Object> requestJson = new HashMap<>();
        requestJson.put("version", "V2");
        requestJson.put("requestId", UUID.randomUUID().toString());
        requestJson.put("timestamp", System.currentTimeMillis());

        Map<String, Object> image = new HashMap<>();
        image.put("format", getImageFormat(imageFile.getOriginalFilename()));
        image.put("name", "image");

        List<Map<String, Object>> images = Arrays.asList(image);
        requestJson.put("images", images);

        // Multipart Body 구성
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", imageFile.getResource())
               .contentType(MediaType.parseMediaType(imageFile.getContentType()));
        builder.part("message", objectMapper.writeValueAsString(requestJson))
               .contentType(MediaType.APPLICATION_JSON);

        // WebClient를 통한 비동기 호출
        Mono<String> responseMono = webClient.post()
                .uri(naverOcrConfig.getApiUrl())
                .header("X-OCR-SECRET", naverOcrConfig.getSecretKey())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    log.error("네이버 OCR API 호출 실패: {}", response.statusCode());
                    return Mono.error(new RuntimeException("OCR API 호출 실패: " + response.statusCode()));
                })
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("네이버 OCR API 호출 성공"));

        // block()을 사용하여 동기식으로 결과 반환 (컨트롤러에서 동기식 처리를 위해)
        String response = responseMono.block();
        log.info("네이버 OCR API 응답: {}", response);
        return response;
    }

    /**
     * 이미지 형식 추출
     */
    private String getImageFormat(String filename) {
        if (filename == null) return "jpg";

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "png": return "png";
            case "jpeg":
            case "jpg": return "jpg";
            case "bmp": return "bmp";
            default: return "jpg";
        }
    }

    /**
     * OCR 응답에서 원본 텍스트 추출
     */
    private String extractRawTextFromResponse(String ocrResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(ocrResponse);
            JsonNode images = rootNode.path("images");

            if (images.isArray() && images.size() > 0) {
                JsonNode fields = images.get(0).path("fields");
                StringBuilder rawText = new StringBuilder();

                for (JsonNode field : fields) {
                    String text = field.path("inferText").asText();
                    if (!text.isEmpty()) {
                        rawText.append(text).append("\n");
                    }
                }

                return rawText.toString();
            }
        } catch (Exception e) {
            log.warn("OCR 응답 텍스트 추출 실패: {}", e.getMessage());
        }

        return "";
    }

    /**
     * OCR 응답에서 텍스트 라인들 추출
     */
    private List<String> extractTextLinesFromResponse(String ocrResponse) {
        List<String> lines = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(ocrResponse);
            JsonNode images = rootNode.path("images");

            if (images.isArray() && images.size() > 0) {
                JsonNode fields = images.get(0).path("fields");

                for (JsonNode field : fields) {
                    String text = field.path("inferText").asText().trim();
                    if (!text.isEmpty()) {
                        lines.add(text);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("OCR 응답 라인 추출 실패: {}", e.getMessage());
        }

        return lines;
    }

    // ===== 파싱 관련 메서드들은 제거됨 =====
    // 복잡한 파싱 로직 대신 ReceiptAnalysisService에서 products 테이블 기반으로 처리
}