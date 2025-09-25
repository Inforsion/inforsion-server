package com.inforsion.inforsionserver.domain.inventory.repository;

import com.inforsion.inforsionserver.domain.inventory.dto.ExpiringInventoryDto;
import com.inforsion.inforsionserver.domain.inventory.dto.InventoryDto;
import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.QInventoryEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    private final QInventoryEntity t = QInventoryEntity.inventoryEntity;

    // 전체 재고 조회 - 페이징 처리: 이름, 현재 재료량, 유통기한, 최근 입고 순 정렬 가능
    @Override
    public Page<InventoryEntity> findInventories(Integer storeId, Pageable pageable) {
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;

        List<OrderSpecifier<?>> sortOrders = getOrderSpecifiers(pageable, inventory);

        List<InventoryEntity> content = queryFactory
                .selectFrom(inventory)
                .where(inventory.store.id.eq(storeId))
                .orderBy(sortOrders.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                queryFactory.select(inventory.count())
                        .from(inventory)
                        .where(inventory.store.id.eq(storeId))
                        .fetchOne()
        ).orElse(0L);
        return new PageImpl<>(content, pageable, total);
    }

    // 동적 정렬
    public List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable, QInventoryEntity inventory){
        return pageable.getSort().stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC:Order.DESC;
                    switch (order.getProperty()){
                        case "name":
                            return (OrderSpecifier<?>) new OrderSpecifier<>(direction, inventory.name);
                        case "currentStock":
                            return (OrderSpecifier<?>) new OrderSpecifier<>(direction, inventory.currentStock);
                        case "expiryDate":
                            return (OrderSpecifier<?>) new OrderSpecifier<>(direction, inventory.expiryDate);
                        case "lastRestockedDate":
                            return (OrderSpecifier<?>) new OrderSpecifier<>(direction, inventory.lastRestockedDate);
                        default:
                            return (OrderSpecifier<?>) new OrderSpecifier<>(direction, inventory.id);
                    }
                })
                .toList();
    }

    // 재고 수정
    @Override
    public Long updateInventory(Integer inventoryId, InventoryDto inventoryDto){
        return queryFactory
                .update(t)
                .set(t.name, inventoryDto.getName())
                .set(t.currentStock, inventoryDto.getCurrentStock())
                .set(t.unit, inventoryDto.getUnit())
                .set(t.unitCost, inventoryDto.getUnitCost())
                .set(t.expiryDate, inventoryDto.getExpiryDate())
                .set(t.lastRestockedDate, inventoryDto.getLastRestockedDate())
                .where(t.id.eq(inventoryId))
                .execute();
    }

    // 재고 삭제
    @Override
    public Long deleteInventory(Integer inventoryId) {
        return queryFactory
                .delete(t)
                .where(t.id.eq(inventoryId))
                .execute();
    }

    // 유통기한 임박
    @Override
    public List<ExpiringInventoryDto> findItemsExpiringBefore(Integer days){
        LocalDate targetDate = LocalDate.now().plusDays(days);

        return queryFactory
                .select(Projections.constructor(
                        ExpiringInventoryDto.class,
                        t.id,
                        t.name,
                        t.expiryDate
                ))
                .from(t)
                .where(t.expiryDate.loe(targetDate))
                .fetch();
    }
}