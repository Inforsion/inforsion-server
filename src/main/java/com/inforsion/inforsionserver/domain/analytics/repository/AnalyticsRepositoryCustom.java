package com.inforsion.inforsionserver.domain.analytics.repository;

import com.inforsion.inforsionserver.domain.analytics.entity.AnalyticsEntity;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsRepositoryCustom {
    
    Page<AnalyticsEntity> findAnalyticsByStoreIdWithPaging(Integer storeId, Pageable pageable);
    
    List<AnalyticsEntity> findAnalyticsByStoreAndPeriodAndDateRange(Integer storeId, PeriodType periodType,
                                                                   LocalDate startDate, LocalDate endDate);
    
    List<AnalyticsEntity> findTopPerformingIngredientsByStore(Integer storeId, int limit);
    
    List<AnalyticsEntity> findLowInventoryTurnoverByStore(Integer storeId);
    
    AnalyticsEntity findLatestAnalyticsByStoreAndIngredient(Integer storeId, String ingredientName);
}