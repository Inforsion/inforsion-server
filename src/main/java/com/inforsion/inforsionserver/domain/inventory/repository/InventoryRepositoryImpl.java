package com.inforsion.inforsionserver.domain.inventory.repository;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.QInventoryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    /**
     * 재고 부족 항목 조회
     * 
     * 특정 가게에서 재고량이 임계값 이하인 재고 항목들을 조회합니다.
     * 재고 부족 알림 시스템이나 자동 발주 시스템에서 사용됩니다.
     * 
     * @param storeId 가게 ID
     * @param threshold 재고 부족 임계값
     * @return 재고가 부족한 항목 목록
     */
    @Override
    public List<InventoryEntity> findLowStockItems(Integer storeId, BigDecimal threshold) {
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        
        return queryFactory
                .selectFrom(inventory)
                .where(inventory.store.id.eq(storeId)
                        .and(inventory.quantity.loe(threshold)))
                .fetch();
    }
    
    /**
     * 유통기한 임박 항목 조회
     * 
     * 특정 가게에서 지정된 날짜 이전에 유통기한이 만료되는 재고 항목들을 조회합니다.
     * 유통기한 빠른 순으로 정렬하여 우선순위를 명확히 합니다.
     * 폐기 방지 및 할인 판매 계획에 사용됩니다.
     * 
     * @param storeId 가게 ID
     * @param beforeDate 기준 날짜 (이 날짜 이전에 만료되는 항목들)
     * @return 유통기한이 임박한 항목 목록 (유통기한 오름차순)
     */
    @Override
    public List<InventoryEntity> findExpiringItems(Integer storeId, LocalDateTime beforeDate) {
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        
        return queryFactory
                .selectFrom(inventory)
                .where(inventory.store.id.eq(storeId)
                        .and(inventory.expiryDate.isNotNull())
                        .and(inventory.expiryDate.before(beforeDate)))
                .orderBy(inventory.expiryDate.asc())
                .fetch();
    }
    
    
    /**
     * 기간별 재고 변동 이력 조회
     * 
     * 특정 가게의 지정된 기간 동안 업데이트된 재고 항목들을 조회합니다.
     * 최신 업데이트 순으로 정렬하여 최근 변동사항을 우선 확인할 수 있습니다.
     * 재고 관리 이력 추적이나 감사에 사용됩니다.
     * 
     * @param storeId 가게 ID
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @return 기간 내 업데이트된 재고 항목 목록 (업데이트 시간 내림차순)
     */
    @Override
    public List<InventoryEntity> findInventoryByStoreAndDateRange(Integer storeId, LocalDateTime startDate, LocalDateTime endDate) {
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        
        return queryFactory
                .selectFrom(inventory)
                .where(inventory.store.id.eq(storeId)
                        .and(inventory.updatedAt.between(startDate, endDate)))
                .orderBy(inventory.updatedAt.desc())
                .fetch();
    }
    
    /**
     * 특정 가게의 만료된 재고 개수 조회
     * 
     * 현재 시점을 기준으로 이미 유통기한이 지난 재고 항목의 개수를 집계합니다.
     * 폐기해야 할 재고량 파악이나 손실 분석에 사용됩니다.
     * 
     * @param storeId 가게 ID
     * @return 만료된 재고 항목 개수
     */
    @Override
    public Long countExpiredInventoryByStoreId(Integer storeId) {
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        
        return queryFactory
                .select(inventory.count())
                .from(inventory)
                .where(inventory.store.id.eq(storeId)
                        .and(inventory.expiryDate.isNotNull())
                        .and(inventory.expiryDate.before(LocalDateTime.now())))
                .fetchOne();
    }
}