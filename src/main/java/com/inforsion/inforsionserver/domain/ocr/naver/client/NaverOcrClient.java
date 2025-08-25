package com.inforsion.inforsionserver.domain.ocr.naver.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.ocr.naver.dto.response.NaverOcrResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class NaverOcrClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public NaverOcrClient(
            @Value("${naver.ocr.api-url}") String apiUrl,
            @Value("${naver.ocr.secret-key}") String secretKey,
            ObjectMapper objectMapper
    ) {
        // API URL에서 base URL과 path 분리
        String baseUrl = apiUrl.substring(0, apiUrl.lastIndexOf("/"));
        
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-OCR-SECRET", secretKey)
                .build();
        this.objectMapper = objectMapper;
        
        log.info("NaverOcrClient 초기화 완료 - Base URL: {}", baseUrl);
    }

    public String parseText(byte[] imageBytes, String filename) {
        try {
            log.info("네이버 OCR 처리 시작 - 이미지 크기: {} bytes, 파일명: {}", imageBytes.length, filename);
            
            // 1) Build message payload for /general
            Map<String, Object> payload = new HashMap<>();
            payload.put("version", "V2");
            payload.put("requestId", UUID.randomUUID().toString());
            payload.put("timestamp", System.currentTimeMillis());
            payload.put("lang", "ko");
            payload.put("enableTableDetection", false);
            
            String imageFormat = getImageFormat(filename);
            List<Map<String, String>> images = List.of(
                    Map.of("format", imageFormat, "name", filename != null ? filename : "image")
            );
            payload.put("images", images);
            String messageJson = objectMapper.writeValueAsString(payload);

            // 2) Build multipart body
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("message", messageJson)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

            ByteArrayResource resource = new ByteArrayResource(imageBytes) {
                @Override 
                public String getFilename() { 
                    return filename != null ? filename : "image." + imageFormat; 
                }
            };
            
            builder.part("file", resource)
                    .header(HttpHeaders.CONTENT_TYPE, getMediaType(imageFormat).toString())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.builder("form-data")
                                    .name("file")
                                    .filename(resource.getFilename())
                                    .build()
                                    .toString()
                    );

            MultiValueMap<String, HttpEntity<?>> parts = builder.build();

            // 3) Execute POST /general
            log.debug("네이버 OCR API 호출 시작");
            NaverOcrResponse response = webClient.post()
                    .uri("/general")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(parts))
                    .retrieve()
                    .bodyToMono(NaverOcrResponse.class)
                    .block();

            if (response == null || response.getImages() == null || response.getImages().isEmpty()) {
                throw new RuntimeException("OCR parsing failed: empty response");
            }

            // 4) Concatenate recognized text with line breaks
            StringBuilder sb = new StringBuilder();
            response.getImages().forEach(img -> {
                if (img.getFields() != null) {
                    img.getFields().forEach(field -> {
                        if (field.getInferText() != null && !field.getInferText().trim().isEmpty()) {
                            sb.append(field.getInferText())
                                    .append(System.lineSeparator());
                        }
                    });
                }
            });
            
            String result = sb.toString().trim();
            log.info("네이버 OCR 처리 완료 - 추출된 텍스트 길이: {} 문자", result.length());
            return result;

        } catch (WebClientResponseException e) {
            log.error("네이버 OCR API 오류 - 상태: {}, 응답: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OCR service error [" + e.getStatusCode() + "]: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("네이버 OCR 처리 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse OCR multipart request: " + e.getMessage(), e);
        }
    }
    
    /**
     * 파일명에서 이미지 포맷 추출
     */
    private String getImageFormat(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "png";
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "jpg";
            case "png":
                return "png";
            case "bmp":
                return "bmp";
            case "tiff":
            case "tif":
                return "tiff";
            default:
                return "png";
        }
    }
    
    /**
     * 이미지 포맷에 따른 MediaType 반환
     */
    private MediaType getMediaType(String format) {
        switch (format.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}