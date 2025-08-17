package com.inforsion.inforsionserver.domain.transaction.controller;

import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionCreateRequest;
import com.inforsion.inforsionserver.domain.transaction.dto.response.TransactionResponse;
import com.inforsion.inforsionserver.domain.transaction.service.StoreTransactionService;
import com.inforsion.inforsionserver.global.enums.TransactionCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/transactions")
@RequiredArgsConstructor
@Tag(name = "Store Transactions", description = "가게 매출/원가 관리 API")
public class StoreTransactionController {

    private final StoreTransactionService transactionService;

    @PostMapping
    @Operation(summary = "거래 생성", description = "새로운 매출/원가 거래를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "거래 생성 성공")
    public ResponseEntity<TransactionResponse> createTransaction(
            @Parameter(description = "가게 ID") @PathVariable Integer storeId,
            @Valid @RequestBody TransactionCreateRequest request) {
        
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "거래 목록 조회", description = "가게의 모든 거래 내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "거래 목록 조회 성공")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @Parameter(description = "가게 ID") @PathVariable Integer storeId) {
        
        List<TransactionResponse> response = transactionService.getTransactionsByStore(storeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "거래 상세 조회", description = "특정 거래의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "거래 조회 성공")
    public ResponseEntity<TransactionResponse> getTransaction(
            @Parameter(description = "가게 ID") @PathVariable Integer storeId,
            @Parameter(description = "거래 ID") @PathVariable Integer transactionId) {
        
        TransactionResponse response = transactionService.getTransaction(storeId, transactionId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{transactionId}")
    @Operation(summary = "거래 삭제", description = "기존 거래를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "거래 삭제 성공")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "가게 ID") @PathVariable Integer storeId,
            @Parameter(description = "거래 ID") @PathVariable Integer transactionId) {
        
        transactionService.deleteTransaction(storeId, transactionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    @Operation(summary = "거래 카테고리 목록 조회", description = "사용 가능한 모든 거래 카테고리를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    public ResponseEntity<List<TransactionCategory>> getCategories() {
        List<TransactionCategory> categories = Arrays.asList(TransactionCategory.values());
        return ResponseEntity.ok(categories);
    }
}