package com.inforsion.inforsionserver.domain.inventory.repository;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryLogEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.QInventoryLogEntity;
import com.inforsion.inforsionserver.global.enums.InventoryLogType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InventoryLogRepositoryImpl implements InventoryLogRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QInventoryLogEntity qLog = QInventoryLogEntity.inventoryLogEntity;

    @Override
    public Page<InventoryLogEntity> findLogsByInventoryIdWithPaging(Integer inventoryId, Pageable pageable) {
        List<InventoryLogEntity> logs = queryFactory
                .selectFrom(qLog)
                .where(qLog.inventory.id.eq(inventoryId))
                .orderBy(qLog.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qLog.count())
                .from(qLog)
                .where(qLog.inventory.id.eq(inventoryId))
                .fetchOne();

        return new PageImpl<>(logs, pageable, total != null ? total : 0);
    }

    @Override
    public Page<InventoryLogEntity> findLogsByStoreIdWithPaging(Integer storeId, Pageable pageable) {
        List<InventoryLogEntity> logs = queryFactory
                .selectFrom(qLog)
                .where(qLog.inventory.store.id.eq(storeId))
                .orderBy(qLog.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qLog.count())
                .from(qLog)
                .where(qLog.inventory.store.id.eq(storeId))
                .fetchOne();

        return new PageImpl<>(logs, pageable, total != null ? total : 0);
    }

    @Override
    public List<InventoryLogEntity> findLogsByInventoryAndTypeAndDateRange(Integer inventoryId, InventoryLogType logType,
                                                                          LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .selectFrom(qLog)
                .where(qLog.inventory.id.eq(inventoryId)
                        .and(qLog.logType.eq(logType))
                        .and(qLog.createdAt.between(startDate, endDate)))
                .orderBy(qLog.createdAt.desc())
                .fetch();
    }

    @Override
    public List<InventoryLogEntity> findLogsByStoreAndDateRange(Integer storeId, LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .selectFrom(qLog)
                .where(qLog.inventory.store.id.eq(storeId)
                        .and(qLog.createdAt.between(startDate, endDate)))
                .orderBy(qLog.createdAt.desc())
                .fetch();
    }

    @Override
    public List<InventoryLogEntity> findRecentLogsByInventoryId(Integer inventoryId, int limit) {
        return queryFactory
                .selectFrom(qLog)
                .where(qLog.inventory.id.eq(inventoryId))
                .orderBy(qLog.createdAt.desc())
                .limit(limit)
                .fetch();
    }
}