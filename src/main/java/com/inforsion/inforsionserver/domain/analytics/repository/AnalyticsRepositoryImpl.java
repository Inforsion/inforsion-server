package com.inforsion.inforsionserver.domain.analytics.repository;

import com.inforsion.inforsionserver.domain.analytics.entity.AnalyticsEntity;
import com.inforsion.inforsionserver.domain.analytics.entity.QAnalyticsEntity;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AnalyticsRepositoryImpl implements AnalyticsRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QAnalyticsEntity qAnalytics = QAnalyticsEntity.analyticsEntity;

    @Override
    public Page<AnalyticsEntity> findAnalyticsByStoreIdWithPaging(Integer storeId, Pageable pageable) {
        List<AnalyticsEntity> analytics = queryFactory
                .selectFrom(qAnalytics)
                .where(qAnalytics.store.id.eq(storeId))
                .orderBy(qAnalytics.analysisDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qAnalytics.count())
                .from(qAnalytics)
                .where(qAnalytics.store.id.eq(storeId))
                .fetchOne();

        return new PageImpl<>(analytics, pageable, total != null ? total : 0);
    }

    @Override
    public List<AnalyticsEntity> findAnalyticsByStoreAndPeriodAndDateRange(Integer storeId, PeriodType periodType,
                                                                          LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(qAnalytics)
                .where(qAnalytics.store.id.eq(storeId)
                        .and(qAnalytics.periodType.eq(periodType))
                        .and(qAnalytics.analysisDate.between(startDate, endDate)))
                .orderBy(qAnalytics.analysisDate.desc())
                .fetch();
    }

    @Override
    public List<AnalyticsEntity> findTopPerformingIngredientsByStore(Integer storeId, int limit) {
        return queryFactory
                .selectFrom(qAnalytics)
                .where(qAnalytics.store.id.eq(storeId)
                        .and(qAnalytics.inventoryTurnover.isNotNull()))
                .orderBy(qAnalytics.inventoryTurnover.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<AnalyticsEntity> findLowInventoryTurnoverByStore(Integer storeId) {
        return queryFactory
                .selectFrom(qAnalytics)
                .where(qAnalytics.store.id.eq(storeId)
                        .and(qAnalytics.inventoryTurnover.isNotNull())
                        .and(qAnalytics.inventoryTurnover.lt(BigDecimal.valueOf(1.0))))
                .orderBy(qAnalytics.inventoryTurnover.asc())
                .fetch();
    }

    @Override
    public AnalyticsEntity findLatestAnalyticsByStoreAndIngredient(Integer storeId, String ingredientName) {
        return queryFactory
                .selectFrom(qAnalytics)
                .where(qAnalytics.store.id.eq(storeId)
                        .and(qAnalytics.ingredientName.eq(ingredientName)))
                .orderBy(qAnalytics.analysisDate.desc())
                .fetchFirst();
    }
}