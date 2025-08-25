package com.inforsion.inforsionserver.domain.ocr.naver.controller;

import com.inforsion.inforsionserver.domain.ocr.mongo.entity.OcrResultEntity;
import com.inforsion.inforsionserver.domain.ocr.mongo.repository.OcrResultRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Tag(name = "OCR History", description = "OCR 결과 히스토리 관리 API")
@RestController
@RequestMapping("/api/v1/ocr/history")
@RequiredArgsConstructor
@Slf4j
public class OcrHistoryController {

    private final OcrResultRepository ocrResultRepository;

    @Operation(summary = "OCR 결과 전체 조회", description = "저장된 모든 OCR 결과를 페이지네이션으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "OCR 결과 조회 성공")
    @GetMapping
    public ResponseEntity<Page<OcrResultEntity>> getAllOcrResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OcrResultEntity> results = ocrResultRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        log.info("OCR 결과 전체 조회 - 페이지: {}, 크기: {}, 총 개수: {}", page, size, results.getTotalElements());
        
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "OCR 결과 ID로 조회", description = "특정 ID의 OCR 결과를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "OCR 결과 조회 성공")
    @ApiResponse(responseCode = "404", description = "OCR 결과를 찾을 수 없음")
    @GetMapping("/{id}")
    public ResponseEntity<OcrResultEntity> getOcrResultById(@PathVariable String id) {
        Optional<OcrResultEntity> result = ocrResultRepository.findById(id);
        
        if (result.isPresent()) {
            log.info("OCR 결과 ID 조회 성공 - ID: {}", id);
            return ResponseEntity.ok(result.get());
        } else {
            log.warn("OCR 결과를 찾을 수 없음 - ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "파일명으로 OCR 결과 조회", description = "특정 파일명의 OCR 결과를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "OCR 결과 조회 성공")
    @GetMapping("/by-filename/{filename}")
    public ResponseEntity<List<OcrResultEntity>> getOcrResultsByFilename(@PathVariable String filename) {
        List<OcrResultEntity> results = ocrResultRepository.findByOriginalFileName(filename);
        
        log.info("파일명으로 OCR 결과 조회 - 파일명: {}, 결과 개수: {}", filename, results.size());
        
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "OCR 엔진별 결과 조회", description = "특정 OCR 엔진의 결과를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "OCR 결과 조회 성공")
    @GetMapping("/by-engine/{engine}")
    public ResponseEntity<List<OcrResultEntity>> getOcrResultsByEngine(@PathVariable String engine) {
        List<OcrResultEntity> results = ocrResultRepository.findByOcrEngine(engine);
        
        log.info("OCR 엔진별 결과 조회 - 엔진: {}, 결과 개수: {}", engine, results.size());
        
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "기간별 OCR 결과 조회", description = "특정 기간의 OCR 결과를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "OCR 결과 조회 성공")
    @GetMapping("/by-date-range")
    public ResponseEntity<List<OcrResultEntity>> getOcrResultsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        List<OcrResultEntity> results = ocrResultRepository.findByCreatedAtBetween(start, end);
        
        log.info("기간별 OCR 결과 조회 - 시작: {}, 종료: {}, 결과 개수: {}", startDate, endDate, results.size());
        
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "OCR 결과 삭제", description = "특정 ID의 OCR 결과를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "OCR 결과 삭제 성공")
    @ApiResponse(responseCode = "404", description = "OCR 결과를 찾을 수 없음")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOcrResult(@PathVariable String id) {
        if (ocrResultRepository.existsById(id)) {
            ocrResultRepository.deleteById(id);
            log.info("OCR 결과 삭제 성공 - ID: {}", id);
            return ResponseEntity.ok().build();
        } else {
            log.warn("삭제할 OCR 결과를 찾을 수 없음 - ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}