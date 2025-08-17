package com.inforsion.inforsionserver.domain.transaction.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SalesAnalyticsRequest {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    // 기본값: 오늘 날짜
    public LocalDate getStartDate() {
        return startDate != null ? startDate : LocalDate.now();
    }
    
    public LocalDate getEndDate() {
        return endDate != null ? endDate : LocalDate.now();
    }
}