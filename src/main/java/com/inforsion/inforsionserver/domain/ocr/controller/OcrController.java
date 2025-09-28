package com.inforsion.inforsionserver.domain.ocr.controller;

import com.inforsion.inforsionserver.domain.ocr.dto.OcrConfirmationRequestDto;
import com.inforsion.inforsionserver.domain.ocr.dto.ProductMatchingResultDto;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrResultEntity;
import com.inforsion.inforsionserver.domain.ocr.service.OcrProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "OCR Processing", description = "OCR 영수증 처리 API (3단계)")
@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
@Slf4j
public class OcrController {

    private final OcrProcessingService ocrProcessingService;

    // ===== 1단계: 이미지 OCR + 제품 매칭 =====
    @Operation(summary = "이미지 OCR 처리", description = "이미지에서 글자 인식 → products 테이블 비교 → 제품 매칭 결과 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ProductMatchingResultDto> processImageOcr(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("image") MultipartFile imageFile,
            @Parameter(description = "매장 ID", required = true)
            @RequestParam("storeId") Integer storeId,
            @Parameter(description = "문서 타입 (RECEIPT/INVOICE)", required = true)
            @RequestParam("documentType") String documentType) {

        try {
            log.info("1단계: 이미지 OCR 처리 시작 - 매장 ID: {}, 파일명: {}",
                    storeId, imageFile.getOriginalFilename());

            ProductMatchingResultDto result = ocrProcessingService.processImageOcr(imageFile, storeId, documentType);

            log.info("1단계: OCR 처리 완료 - rawDataId: {}, 매칭된 아이템 수: {}",
                    result.getRawDataId(), result.getMatchedItems().size());

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.warn("이미지 OCR 처리 요청 데이터 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("이미지 OCR 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== 1.5단계: 매칭 결과 미리보기 (수정용) =====
    @Operation(summary = "OCR 매칭 결과 미리보기", description = "1단계 결과를 사용자 수정용으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "원본 데이터를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/preview/{rawDataId}")
    public ResponseEntity<ProductMatchingResultDto> getOcrPreview(
            @Parameter(description = "원본 데이터 ID", required = true)
            @PathVariable Integer rawDataId) {

        try {
            log.info("1.5단계: OCR 매칭 결과 미리보기 - rawDataId: {}", rawDataId);

            ProductMatchingResultDto preview = ocrProcessingService.getOcrPreview(rawDataId);

            log.info("1.5단계: 미리보기 완료 - rawDataId: {}, 매칭된 아이템 수: {}",
                    rawDataId, preview.getMatchedItems().size());

            return ResponseEntity.ok(preview);

        } catch (IllegalArgumentException e) {
            log.warn("OCR 미리보기 요청 데이터 오류: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("OCR 미리보기 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== 2단계: 사용자 확인 + 최종 확정 + 재고 적용 =====
    @Operation(summary = "OCR 결과 확정", description = "사용자 수정/확인 → 최종 확정 → inventories 재고 차감")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "확정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 확정 데이터"),
            @ApiResponse(responseCode = "404", description = "원본 데이터를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmOcrResults(
            @Parameter(description = "OCR 결과 확정 데이터", required = true)
            @RequestBody OcrConfirmationRequestDto confirmationDto) {

        try {
            log.info("2단계: OCR 결과 확정 시작 - rawDataId: {}, 확정 아이템 수: {}",
                    confirmationDto.getRawDataId(), confirmationDto.getConfirmedItems().size());

            ocrProcessingService.confirmOcrResults(confirmationDto);

            log.info("2단계: OCR 결과 확정 완료 - rawDataId: {}", confirmationDto.getRawDataId());

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            log.warn("OCR 결과 확정 요청 데이터 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("OCR 결과 확정 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== 3단계: 처리 이력 조회 =====
    @Operation(summary = "매장 OCR 처리 이력", description = "특정 매장의 OCR 처리 이력을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "매장을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/history/{storeId}")
    public ResponseEntity<List<OcrResultEntity>> getOcrHistory(
            @Parameter(description = "매장 ID", required = true)
            @PathVariable Integer storeId) {

        try {
            List<OcrResultEntity> history = ocrProcessingService.getOcrHistory(storeId);

            log.info("3단계: OCR 처리 이력 조회 완료 - 매장 ID: {}, 이력 수: {}", storeId, history.size());

            return ResponseEntity.ok(history);

        } catch (Exception e) {
            log.error("OCR 처리 이력 조회 중 오류 발생 - 매장 ID: {}, 오류: {}", storeId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}