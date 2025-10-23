package com.inforsion.inforsionserver.domain.report.controller;

import com.inforsion.inforsionserver.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import com.inforsion.inforsionserver.global.enums.PeriodType;

@RestController
@RequestMapping("/api/v1/report")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "리포트 생성",
            description = "리포트를 생성하기 위해 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF 리포트 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generateReportPdf(
            @RequestParam Integer storeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "MONTH") PeriodType periodType
    ) {
        try {
            byte[] pdfData = reportService.generateReportPdf(storeId, startDate, endDate, periodType);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=financial-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}