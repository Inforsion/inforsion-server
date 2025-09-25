package com.inforsion.inforsionserver.domain.alert.repository;

import com.inforsion.inforsionserver.domain.alert.entity.AlertEntity;
import com.inforsion.inforsionserver.domain.alert.entity.QAlertEntity;
import com.inforsion.inforsionserver.global.enums.AlertType;
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
public class AlertRepositoryImpl implements AlertRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QAlertEntity qAlert = QAlertEntity.alertEntity;

    @Override
    public Page<AlertEntity> findAlertsByStoreIdWithPaging(Integer storeId, Pageable pageable) {
        List<AlertEntity> alerts = queryFactory
                .selectFrom(qAlert)
                .where(qAlert.store.id.eq(storeId))
                .orderBy(qAlert.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qAlert.count())
                .from(qAlert)
                .where(qAlert.store.id.eq(storeId))
                .fetchOne();

        return new PageImpl<>(alerts, pageable, total != null ? total : 0);
    }

    @Override
    public Page<AlertEntity> findUnreadAlertsByUserIdWithPaging(Integer userId, Pageable pageable) {
        List<AlertEntity> alerts = queryFactory
                .selectFrom(qAlert)
                .where(qAlert.user.id.eq(userId)
                        .and(qAlert.isRead.eq(false)))
                .orderBy(qAlert.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qAlert.count())
                .from(qAlert)
                .where(qAlert.user.id.eq(userId)
                        .and(qAlert.isRead.eq(false)))
                .fetchOne();

        return new PageImpl<>(alerts, pageable, total != null ? total : 0);
    }

    @Override
    public List<AlertEntity> findAlertsByStoreAndTypeAndDateRange(Integer storeId, AlertType alertType,
                                                                 LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .selectFrom(qAlert)
                .where(qAlert.store.id.eq(storeId)
                        .and(qAlert.alertType.eq(alertType))
                        .and(qAlert.createdAt.between(startDate, endDate)))
                .orderBy(qAlert.createdAt.desc())
                .fetch();
    }

    @Override
    public void markAllAlertsAsReadByUserId(Integer userId) {
        queryFactory
                .update(qAlert)
                .set(qAlert.isRead, true)
                .set(qAlert.readAt, LocalDateTime.now())
                .where(qAlert.user.id.eq(userId)
                        .and(qAlert.isRead.eq(false)))
                .execute();
    }

    @Override
    public Long countUnreadAlertsByStoreId(Integer storeId) {
        return queryFactory
                .select(qAlert.count())
                .from(qAlert)
                .where(qAlert.store.id.eq(storeId)
                        .and(qAlert.isRead.eq(false)))
                .fetchOne();
    }
}