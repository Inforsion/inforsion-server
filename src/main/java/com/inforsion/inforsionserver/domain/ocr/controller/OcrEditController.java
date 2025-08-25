package com.inforsion.inforsionserver.domain.ocr.controller;

import com.inforsion.inforsionserver.domain.ocr.dto.OcrEditRequest;
import com.inforsion.inforsionserver.domain.ocr.dto.OcrEditResponse;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrEditedResultEntity;
import com.inforsion.inforsionserver.domain.ocr.service.OcrEditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "OCR Edit", description = "OCR 결과 편집 및 관리 API")
@RestController
@RequestMapping("/api/v1/ocr/edit")
@RequiredArgsConstructor
@Slf4j
public class OcrEditController {

    private final OcrEditService ocrEditService;

    @Operation(summary = "OCR 결과 편집용 데이터 조회", description = "MongoDB의 OCR 결과를 편집할 수 있도록 조회합니다.")
    @ApiResponse(responseCode = "200", description = "OCR 편집 데이터 조회 성공")
    @ApiResponse(responseCode = "404", description = "OCR 결과를 찾을 수 없음")
    @GetMapping("/prepare/{ocrResultId}")
    public ResponseEntity<OcrEditResponse> prepareOcrEdit(@PathVariable String ocrResultId) {
        try {
            OcrEditResponse response = ocrEditService.getOcrDataForEdit(ocrResultId);
            log.info("OCR 편집용 데이터 조회 성공 - MongoDB OCR ID: {}", ocrResultId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("OCR 편집용 데이터 조회 실패 - MongoDB OCR ID: {}, 오류: {}", ocrResultId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "OCR 결과 편집 저장", description = "사용자가 수정한 OCR 결과를 MySQL에 저장합니다.")
    @ApiResponse(responseCode = "200", description = "OCR 편집 결과 저장 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "404", description = "OCR 결과 또는 사용자를 찾을 수 없음")
    @PostMapping("/save")
    public ResponseEntity<OcrEditResponse> saveEditedOcrResult(@Valid @RequestBody OcrEditRequest request) {
        try {
            OcrEditResponse response = ocrEditService.saveEditedOcrResult(request);
            log.info("OCR 편집 결과 저장 성공 - MongoDB OCR ID: {}, 사용자 ID: {}", 
                    request.getOcrResultId(), request.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("OCR 편집 결과 저장 실패 - MongoDB OCR ID: {}, 사용자 ID: {}, 오류: {}", 
                    request.getOcrResultId(), request.getUserId(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "편집된 OCR 결과 전체 조회", description = "저장된 모든 편집된 OCR 결과를 페이지네이션으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "편집된 OCR 결과 조회 성공")
    @GetMapping("/results")
    public ResponseEntity<Page<OcrEditedResultEntity>> getAllEditedResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OcrEditedResultEntity> results = ocrEditService.getAllEditedResults(pageable);
        
        log.info("편집된 OCR 결과 전체 조회 - 페이지: {}, 크기: {}, 총 개수: {}", page, size, results.getTotalElements());
        
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "사용자별 편집된 OCR 결과 조회", description = "특정 사용자의 편집된 OCR 결과를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자별 편집된 OCR 결과 조회 성공")
    @GetMapping("/results/user/{userId}")
    public ResponseEntity<Page<OcrEditedResultEntity>> getUserEditedResults(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OcrEditedResultEntity> results = ocrEditService.getUserEditedResults(userId, pageable);
        
        log.info("사용자별 편집된 OCR 결과 조회 - 사용자 ID: {}, 페이지: {}, 크기: {}, 총 개수: {}", 
                userId, page, size, results.getTotalElements());
        
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "편집된 OCR 결과 상세 조회", description = "특정 ID의 편집된 OCR 결과를 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "편집된 OCR 결과 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "편집된 OCR 결과를 찾을 수 없음")
    @GetMapping("/results/{id}")
    public ResponseEntity<OcrEditedResultEntity> getEditedResultById(@PathVariable Long id) {
        Optional<OcrEditedResultEntity> result = ocrEditService.getEditedResultById(id);
        
        if (result.isPresent()) {
            log.info("편집된 OCR 결과 상세 조회 성공 - ID: {}", id);
            return ResponseEntity.ok(result.get());
        } else {
            log.warn("편집된 OCR 결과를 찾을 수 없음 - ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "편집된 OCR 결과 삭제", description = "특정 ID의 편집된 OCR 결과를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "편집된 OCR 결과 삭제 성공")
    @ApiResponse(responseCode = "404", description = "편집된 OCR 결과를 찾을 수 없음")
    @DeleteMapping("/results/{id}")
    public ResponseEntity<Void> deleteEditedResult(@PathVariable Long id) {
        try {
            ocrEditService.deleteEditedResult(id);
            log.info("편집된 OCR 결과 삭제 성공 - ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("편집된 OCR 결과 삭제 실패 - ID: {}, 오류: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}