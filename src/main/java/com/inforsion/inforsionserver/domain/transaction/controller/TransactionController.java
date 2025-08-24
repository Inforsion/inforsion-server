package com.inforsion.inforsionserver.domain.transaction.controller;

import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionRequestDto;
import com.inforsion.inforsionserver.domain.transaction.dto.response.TransactionResponseDto;
import com.inforsion.inforsionserver.domain.transaction.service.TransactionService;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    ////////////////////////////////////////////////////
    /////                 거래 생성                 //////
    ///////////////////////////////////////////////////
    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @Valid @RequestBody TransactionRequestDto requestDto
    ){
        TransactionResponseDto created = transactionService.createTransaction(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    ////////////////////////////////////////////////////
    /////                 거래 조회                 //////
    ///////////////////////////////////////////////////
    @GetMapping("/{storeId}")
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(
            @PathVariable Integer storeId,
            @RequestParam TransactionType transactionType,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
            )
    {
        List<TransactionResponseDto> transactions = transactionService.getTransaction(
                storeId, transactionType, startDate, endDate
        );

        return ResponseEntity.ok(transactions);
    }

    ////////////////////////////////////////////////////
    /////                 거래 수정                 //////
    ///////////////////////////////////////////////////
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> updateTransaction(
        @PathVariable Integer id,
                @RequestBody TransactionRequestDto requestDto){
        TransactionResponseDto transactionResponseDto = transactionService.updateTransaction(id, requestDto);
        return ResponseEntity.ok(transactionResponseDto);
    }


    ////////////////////////////////////////////////////
    /////                거래 삭제                  //////
    ///////////////////////////////////////////////////
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id){
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

}
