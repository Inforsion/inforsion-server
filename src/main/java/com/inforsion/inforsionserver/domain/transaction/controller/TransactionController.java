package com.inforsion.inforsionserver.domain.transaction.controller;

import com.inforsion.inforsionserver.domain.transaction.dto.response.StoreSalesFinancialDto;
import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionConditionDto;
import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionRequestDto;
import com.inforsion.inforsionserver.domain.transaction.dto.response.TransactionResponseDto;
import com.inforsion.inforsionserver.domain.transaction.service.TransactionService;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Transaction", description = "매장 거래 관리 API - 수입/지출 거래 내역 관리")
@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;

    @Operation(
            summary = "거래 목록 조회",
            description = "지정된 매장의 거래 내역을 조건에 따라 조회합니다. 거래 유형(수입/지출)과 기간을 필터로 사용할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "거래 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "매장을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{storeId}")
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(
            @Parameter(description = "매장 ID", required = true, example = "1")
            @PathVariable Integer storeId,
            @Parameter(description = "거래 유형 (INCOME: 수입, EXPENSE: 지출)", required = true, example = "INCOME")
            @RequestParam TransactionType transactionType,
            @Parameter(description = "조회 시작 날짜", required = true, example = "2025-08-01T00:00:00")
            @RequestParam LocalDateTime startDate,
            @Parameter(description = "조회 종료 날짜", required = true, example = "2025-08-31T23:59:59")
            @RequestParam LocalDateTime endDate
    ) {
        List<TransactionResponseDto> transactions = transactionService.getTransaction(
                storeId, transactionType, startDate, endDate
        );
        return ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "거래 수정",
            description = "기존 거래 정보를 수정합니다. 거래 금액, 설명, 유형 등을 변경할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "거래 수정 성공"),
            @ApiResponse(responseCode = "404", description = "거래를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{transId}")
    public ResponseEntity<TransactionResponseDto> updateTransaction(
            @Parameter(description = "거래 ID", required = true, example = "1")
            @PathVariable Integer transId,
            @Parameter(description = "거래 수정 요청 데이터", required = true)
            @Valid @RequestBody TransactionRequestDto requestDto
    ) {
        TransactionResponseDto transactionResponseDto = transactionService.updateTransaction(transId, requestDto);
        return ResponseEntity.ok(transactionResponseDto);
    }

    @Operation(
            summary = "거래 삭제",
            description = "지정된 거래를 삭제합니다. 삭제된 거래는 복구할 수 없습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "거래 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "거래를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{transId}")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "삭제할 거래 ID", required = true, example = "1")
            @PathVariable("transId") Integer transId
    ) {
        transactionService.deleteTransaction(transId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "매출 계산",
            description = "매출을 확인할 수 있습니다.."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "거래 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "거래를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/financials")
    public ResponseEntity<List<StoreSalesFinancialDto>> getStoreFinancials(
            @ModelAttribute TransactionConditionDto condition,
            @RequestParam PeriodType periodType
            ){
        List<StoreSalesFinancialDto> result = transactionService.getStoreFinancials(condition, periodType);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "거래 생성",
            description = "새로운 거래(수입/지출)를 생성합니다. 매장의 수익 및 비용 관리를 위해 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "거래 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @Parameter(description = "거래 생성 요청 데이터", required = true)
            @Valid @RequestBody TransactionRequestDto requestDto
    ) {
        TransactionResponseDto created = transactionService.createTransaction(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
