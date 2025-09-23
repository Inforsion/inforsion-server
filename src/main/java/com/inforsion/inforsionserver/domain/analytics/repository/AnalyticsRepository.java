package com.inforsion.inforsionserver.domain.analytics.repository;

import com.inforsion.inforsionserver.domain.analytics.entity.AnalyticsEntity;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<AnalyticsEntity, Integer>, AnalyticsRepositoryCustom {

    List<AnalyticsEntity> findByStoreIdOrderByAnalysisDateDesc(Integer storeId);

    List<AnalyticsEntity> findByStoreIdAndPeriodTypeOrderByAnalysisDateDesc(Integer storeId, PeriodType periodType);

    List<AnalyticsEntity> findByStoreIdAndIngredientNameOrderByAnalysisDateDesc(Integer storeId, String ingredientName);

    Optional<AnalyticsEntity> findByStoreIdAndAnalysisDateAndPeriodTypeAndIngredientName(
            Integer storeId, LocalDate analysisDate, PeriodType periodType, String ingredientName);

    @Query("SELECT a FROM AnalyticsEntity a WHERE a.store.id = :storeId AND a.analysisDate BETWEEN :startDate AND :endDate ORDER BY a.analysisDate DESC")
    List<AnalyticsEntity> findByStoreIdAndAnalysisDateBetween(@Param("storeId") Integer storeId,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM AnalyticsEntity a WHERE a.store.id = :storeId AND a.periodType = :periodType AND a.analysisDate BETWEEN :startDate AND :endDate ORDER BY a.analysisDate DESC")
    List<AnalyticsEntity> findByStoreIdAndPeriodTypeAndAnalysisDateBetween(@Param("storeId") Integer storeId,
                                                                          @Param("periodType") PeriodType periodType,
                                                                          @Param("startDate") LocalDate startDate,
                                                                          @Param("endDate") LocalDate endDate);
}