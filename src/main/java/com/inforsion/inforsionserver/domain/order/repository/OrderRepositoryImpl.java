package com.inforsion.inforsionserver.domain.order.repository;

import com.inforsion.inforsionserver.domain.order.dto.request.OrderRequestDto;
import com.inforsion.inforsionserver.domain.order.entity.OrderEntity;
import com.inforsion.inforsionserver.domain.order.entity.QOrderEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QOrderEntity t = QOrderEntity.orderEntity;

    public OrderRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 주문 조회 - 페이징 처리
     * **/
    @Override
    public Page<OrderEntity> findOrders(Integer storeId, Pageable pageable) {
        QOrderEntity order = QOrderEntity.orderEntity;

        List<OrderSpecifier<?>> sortOrders = getOrderSpecifiers(pageable, order);

        List<OrderEntity> content = queryFactory
                .selectFrom(order)
                .where(order.store.id.eq(storeId))
                .orderBy(sortOrders.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                queryFactory.select(order.count())
                        .from(order)
                        .where(order.store.id.eq(storeId))
                        .fetchOne()
        ).orElse(0L);
        return new PageImpl<>(content, pageable, total);
    }
    // 동적 정렬
    public List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable, QOrderEntity order){
        return pageable.getSort().stream()
                .map(sort -> {
                    Order direction = sort.isAscending() ? Order.ASC:Order.DESC;
                    switch (sort.getProperty()){
                        case "name":
                            return new OrderSpecifier<>(direction, (ComparableExpressionBase<?>)order.name);
                        default:
                            return new OrderSpecifier<>(direction, (ComparableExpressionBase<?>)order.id);
                    }
                })
                .toList();
    }

    /**
     * 주문 수정
     */
    @Override
    public Long updateOrder(Integer orderId, OrderRequestDto orderRequestDto){
        return queryFactory
                .update(t)
                .set(t.name, orderRequestDto.getName())
                .set(t.subtotal_amount, orderRequestDto.getSubtotal_amount())
                .set(t.total_amount, orderRequestDto.getTotal_amount())
                .set(t.quantity, orderRequestDto.getQuantity())
                .set(t.paymentMethod, orderRequestDto.getPaymentMethod())
                .where(t.id.eq(orderId))
                .execute();
    }

    /**
     * 주문 삭제
     */
    @Override
    public Long deleteOrder(Integer orderId) {
        return queryFactory
                .delete(t)
                .where(t.id.eq(orderId))
                .execute();
    }
}
