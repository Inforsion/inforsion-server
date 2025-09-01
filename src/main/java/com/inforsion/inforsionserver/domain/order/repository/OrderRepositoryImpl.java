package com.inforsion.inforsionserver.domain.order.repository;

import com.inforsion.inforsionserver.domain.transaction.entity.QTransactionEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QTransactionEntity t = QTransactionEntity.transactionEntity;

    public OrderRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 거래 삭제
     */
    @Override
    public Long deleteOrder(Integer OrderId) {
        return queryFactory
                .delete(t)
                .where(t.id.eq(OrderId))
                .execute();
    }
}
