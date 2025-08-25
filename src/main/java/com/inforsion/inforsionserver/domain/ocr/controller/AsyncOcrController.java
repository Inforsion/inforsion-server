package com.inforsion.inforsionserver.domain.ocr.controller;

import com.inforsion.inforsionserver.domain.ocr.dto.OcrJobResult;
import com.inforsion.inforsionserver.domain.ocr.service.AsyncOcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Async OCR", description = "비동기 OCR 처리 API - 고성능 병렬 처리")
@RestController
@RequestMapping("/api/v1/ocr/async")
@RequiredArgsConstructor
@Slf4j
public class AsyncOcrController {

    private final AsyncOcrService asyncOcrService;

    @Operation(
            summary = "비동기 OCR 처리 시작", 
            description = "영수증 이미지를 비동기로 처리하여 즉시 작업 ID를 반환합니다. 고성능 병렬 처리로 빠른 응답을 제공합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "비동기 처리 시작됨 - 작업 ID 반환"),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기 초과"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/process", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> startAsyncOcr(
            @Parameter(
                description = "처리할 영수증 이미지 파일 (JPG, PNG, PDF)", 
                required = true,
                content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary"))
            )
            @RequestParam("file") MultipartFile file) {
        
        long startTime = System.currentTimeMillis();
        
        log.info("비동기 OCR 요청 접수 - 파일명: {}, 크기: {} bytes", 
                file.getOriginalFilename(), file.getSize());

        // 파일 유효성 검증 (빠른 응답을 위해 기본 검증만)
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "빈 파일입니다", "timestamp", System.currentTimeMillis()));
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB 제한
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "파일 크기는 10MB를 초과할 수 없습니다", "timestamp", System.currentTimeMillis()));
        }

        try {
            // Job ID를 미리 생성하여 즉시 응답에 포함
            String jobId = java.util.UUID.randomUUID().toString();
            
            // 비동기 처리 시작 (즉시 반환)
            CompletableFuture<OcrJobResult> future = asyncOcrService.processOcrAsyncWithJobId(file, jobId);
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("jobId", jobId);
            response.put("status", "ACCEPTED");
            response.put("message", "OCR 처리가 시작되었습니다");
            response.put("originalFileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("responseTime", responseTime);
            response.put("statusCheckUrl", "/api/v1/ocr/async/status/" + jobId);
            response.put("timestamp", System.currentTimeMillis());

            log.info("비동기 OCR 작업 접수 완료 - JobId: {}, 응답시간: {}ms", jobId, responseTime);

            return ResponseEntity.accepted().body(response);

        } catch (Exception e) {
            log.error("비동기 OCR 요청 처리 실패 - 파일명: {}, 오류: {}", 
                    file.getOriginalFilename(), e.getMessage(), e);
            
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "OCR 처리 요청에 실패했습니다: " + e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    @Operation(
            summary = "OCR 작업 상태 조회", 
            description = "비동기 OCR 작업의 현재 상태와 진행률을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작업 상태 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 작업 ID"),
    })
    @GetMapping("/status/{jobId}")
    public ResponseEntity<OcrJobResult> getJobStatus(
            @Parameter(description = "조회할 작업 ID", required = true)
            @PathVariable String jobId) {
        
        log.debug("OCR 작업 상태 조회 - JobId: {}", jobId);
        
        try {
            OcrJobResult result = asyncOcrService.getJobStatus(jobId);
            
            if (result.getStatus() == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("작업 상태 조회 실패 - JobId: {}, 오류: {}", jobId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "배치 OCR 처리", 
            description = "여러 영수증 이미지를 동시에 비동기 처리합니다. 최대 10개 파일까지 지원."
    )
    @PostMapping(value = "/batch", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> startBatchOcr(
            @Parameter(
                description = "처리할 영수증 이미지 파일들 (최대 10개)", 
                required = true,
                content = @Content(mediaType = "multipart/form-data")
            )
            @RequestParam("files") List<MultipartFile> files) {
        
        if (files.size() > 10) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "최대 10개 파일까지만 처리 가능합니다"));
        }
        
        long totalSize = files.stream().mapToLong(MultipartFile::getSize).sum();
        if (totalSize > 50 * 1024 * 1024) { // 50MB 제한
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "전체 파일 크기는 50MB를 초과할 수 없습니다"));
        }
        
        log.info("배치 OCR 요청 접수 - 파일 수: {}, 총 크기: {} bytes", files.size(), totalSize);
        
        try {
            CompletableFuture<List<OcrJobResult>> future = asyncOcrService.processBatchAsync(files);
            String batchId = java.util.UUID.randomUUID().toString();
            
            Map<String, Object> response = new HashMap<>();
            response.put("batchId", batchId);
            response.put("status", "ACCEPTED");
            response.put("fileCount", files.size());
            response.put("totalSize", totalSize);
            response.put("message", "배치 OCR 처리가 시작되었습니다");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.accepted().body(response);
            
        } catch (Exception e) {
            log.error("배치 OCR 요청 처리 실패 - 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "배치 OCR 처리 요청에 실패했습니다: " + e.getMessage()));
        }
    }

}