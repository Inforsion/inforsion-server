package com.inforsion.inforsionserver.domain.transaction.controller;

import com.inforsion.inforsionserver.domain.transaction.dto.request.SalesAnalyticsRequest;
import com.inforsion.inforsionserver.domain.transaction.dto.response.SalesAnalyticsResponse;
import com.inforsion.inforsionserver.domain.transaction.service.SalesAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores/{storeId}/analytics")
@RequiredArgsConstructor
@Tag(name = "Sales Analytics", description = "가게 매출 분석 API")
public class SalesAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    @GetMapping("/sales")
    @Operation(
        summary = "매출 분석 조회", 
        description = "가게의 매출, 원가, 순이익, 환불금액 등을 분석합니다. " +
                     "기간을 지정하지 않으면 오늘 날짜로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "매출 분석 조회 성공")
    public ResponseEntity<SalesAnalyticsResponse> getSalesAnalytics(
            @Parameter(description = "가게 ID") @PathVariable Integer storeId,
            @Parameter(description = "조회 기간") @ModelAttribute SalesAnalyticsRequest request) {
        
        SalesAnalyticsResponse response = salesAnalyticsService.getAnalytics(storeId, request);
        return ResponseEntity.ok(response);
    }
}