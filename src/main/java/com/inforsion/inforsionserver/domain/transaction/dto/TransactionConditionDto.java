package com.inforsion.inforsionserver.domain.transaction.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// 사용자가 매출 조회 조건으로 입력한 값 담기
@Data
public class TransactionConditionDto {
    private Integer storeId;
    private Integer id;
    private String storeName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // 시작 날짜

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate; // 마지막 날짜

}
