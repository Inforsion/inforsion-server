package com.inforsion.inforsionserver.domain.ocr.mysql.controller;

import com.inforsion.inforsionserver.domain.ocr.mysql.entity.ReceiptProductEntity;
import com.inforsion.inforsionserver.domain.ocr.mysql.service.ReceiptProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Receipt Products", description = "영수증 제품명 관리 API")
@RestController
@RequestMapping("/api/v1/ocr/products")
@RequiredArgsConstructor
@Slf4j
public class ReceiptProductController {

    private final ReceiptProductService receiptProductService;

    @Operation(summary = "제품명으로 검색", description = "제품명 키워드로 영수증 제품을 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 검색어")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ReceiptProductEntity>> searchProducts(
            @Parameter(description = "검색할 제품명 키워드", required = true)
            @RequestParam String keyword) {
        
        if (keyword == null || keyword.trim().length() < 2) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ReceiptProductEntity> products = receiptProductService.searchByProductName(keyword.trim());
        log.info("제품명 검색 완료 - 키워드: {}, 결과: {}개", keyword, products.size());
        
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "제품명 자동완성", description = "제품명 키워드로 중복 제거된 제품명 목록을 반환합니다.")
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> getProductNameSuggestions(
            @Parameter(description = "자동완성할 제품명 키워드", required = true)
            @RequestParam String keyword) {
        
        if (keyword == null || keyword.trim().length() < 1) {
            return ResponseEntity.badRequest().build();
        }
        
        List<String> suggestions = receiptProductService.findDistinctProductNames(keyword.trim());
        return ResponseEntity.ok(suggestions);
    }

    @Operation(summary = "OCR 작업 ID로 제품 조회", description = "특정 OCR 작업에서 추출된 제품들을 조회합니다.")
    @GetMapping("/by-job/{jobId}")
    public ResponseEntity<List<ReceiptProductEntity>> getProductsByJobId(
            @Parameter(description = "OCR 작업 ID", required = true)
            @PathVariable String jobId) {
        
        List<ReceiptProductEntity> products = receiptProductService.findByOcrJobId(jobId);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "기간별 제품 조회", description = "지정된 기간 동안 추출된 제품들을 페이징으로 조회합니다.")
    @GetMapping("/by-date")
    public ResponseEntity<Page<ReceiptProductEntity>> getProductsByDateRange(
            @Parameter(description = "시작 날짜 (yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "종료 날짜 (yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ReceiptProductEntity> products = receiptProductService.findProductsByDateRange(startDate, endDate, pageable);
        
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "제품명 빈도 통계", description = "특정 제품명이 나타난 횟수를 조회합니다.")
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getProductCount(
            @Parameter(description = "통계를 조회할 제품명", required = true)
            @RequestParam String productName) {
        
        long count = receiptProductService.countByProductName(productName);
        
        Map<String, Object> response = Map.of(
                "productName", productName,
                "count", count,
                "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "키워드로 제품 검색 (상세)", description = "키워드로 제품을 검색하고 상세 정보를 반환합니다.")
    @GetMapping("/search-detailed")
    public ResponseEntity<List<ReceiptProductEntity>> searchDetailedProducts(
            @Parameter(description = "검색할 키워드", required = true)
            @RequestParam String keyword) {
        
        if (keyword == null || keyword.trim().length() < 2) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ReceiptProductEntity> products = receiptProductService.searchByKeyword(keyword.trim());
        log.info("상세 제품 검색 완료 - 키워드: {}, 결과: {}개", keyword, products.size());
        
        return ResponseEntity.ok(products);
    }
}