package com.inforsion.inforsionserver.domain.ocr.naver.controller;

import com.inforsion.inforsionserver.domain.ocr.naver.dto.response.OcrResponse;
import com.inforsion.inforsionserver.domain.ocr.naver.service.NaverOcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Tag(name = "Naver OCR", description = "네이버 OCR API를 이용한 이미지 텍스트 추출")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ocr/naver")
@CrossOrigin(
    originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://10.0.2.2:*", "http://192.168.*.*:*"},
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class NaverOcrController {

    private final NaverOcrService naverOcrService;
    
    // 지원하는 이미지 파일 확장자
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "bmp", "tiff", "tif", "gif"
    );

    @Operation(
            summary = "네이버 OCR 이미지 처리",
            description = "네이버 OCR API를 사용하여 업로드된 이미지 파일에서 텍스트를 추출합니다. 한국어 텍스트 인식에 최적화되어 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OCR 처리 성공",
                    content = @Content(schema = @Schema(implementation = OcrResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 파일 형식 또는 파일 없음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "OCR 처리 중 오류 발생",
                    content = @Content
            )
    })
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OcrResponse> extractTextFromImage(
            @Parameter(
                    description = "OCR 처리할 이미지 파일 (jpg, jpeg, png, bmp, tiff, gif 지원)",
                    required = true
            )
            @RequestParam("image") MultipartFile imageFile) {
        
        try {
            // 파일 검증
            validateImageFile(imageFile);
            
            log.info("네이버 OCR 요청 수신 - 파일명: {}, 크기: {} bytes", 
                    imageFile.getOriginalFilename(), imageFile.getSize());
            
            // OCR 처리
            OcrResponse response = naverOcrService.processImage(imageFile);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("파일 검증 실패: {}", e.getMessage());
            throw new IllegalArgumentException("파일 검증 실패: " + e.getMessage());
        } catch (IOException e) {
            log.error("이미지 파일 읽기 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 파일을 읽을 수 없습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("네이버 OCR 처리 실패: {}", e.getMessage(), e);
            throw new RuntimeException("네이버 OCR 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(
            summary = "네이버 OCR 서비스 상태 확인",
            description = "네이버 OCR 서비스의 현재 상태를 확인합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "서비스 상태 조회 성공",
                    content = @Content
            )
    })
    @GetMapping("/status")
    public ResponseEntity<String> getServiceStatus() {
        return ResponseEntity.ok("네이버 OCR 서비스가 정상적으로 작동 중입니다.");
    }

    /**
     * 이미지 파일 유효성 검증
     */
    private void validateImageFile(MultipartFile file) {
        // 파일이 비어있는지 확인
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 업로드되지 않았습니다.");
        }
        
        // 파일명 확인
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 유효하지 않습니다.");
        }
        
        // 파일 확장자 확인
        String extension = getFileExtension(originalFilename);
        if (!SUPPORTED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    String.format("지원하지 않는 파일 형식입니다. 지원 형식: %s", 
                            String.join(", ", SUPPORTED_EXTENSIONS))
            );
        }
        
        // 파일 크기 확인 (10MB 제한)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다.");
        }
        
        log.info("파일 검증 통과 - 파일명: {}, 확장자: {}, 크기: {} bytes", 
                originalFilename, extension, file.getSize());
    }
    
    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}