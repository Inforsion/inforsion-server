package com.inforsion.inforsionserver.domain.recipes.repository;

import com.inforsion.inforsionserver.domain.recipes.Dto.request.RecipesRequestDto;
import com.inforsion.inforsionserver.domain.recipes.entity.QRecipesEntity;
import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
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
public class RecipesRepositoryImpl implements RecipesRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QRecipesEntity t = QRecipesEntity.recipesEntity;

    public RecipesRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 레시피 조회
     * **/
    @Override
    public Page<RecipesEntity> findRecipes(Integer storeId, Pageable pageable) {
        QRecipesEntity recipes = QRecipesEntity.recipesEntity;
        List<OrderSpecifier<?>> sortRecipes = getOrderSpecifiers(pageable, recipes);

        List<RecipesEntity> content = queryFactory
                .selectFrom(recipes)
                .where(recipes.store.id.eq(storeId))
                .orderBy(sortRecipes.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                queryFactory.select(recipes.count())
                        .from(recipes)
                        .where(recipes.store.id.eq(storeId))
                        .fetchOne()
        ).orElse(0L);
        return new PageImpl<>(content, pageable, total);
    }
    public List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable, QRecipesEntity recipes){
        return pageable.getSort().stream()
                .map(sort -> {
                    Order direction = sort.isAscending() ? Order.ASC:Order.DESC;
                    switch (sort.getProperty()){
                        case "name":
                            return new OrderSpecifier<>(direction, (ComparableExpressionBase<?>)recipes.name);
                        case "createdAt":
                            return new OrderSpecifier<>(direction, (ComparableExpressionBase<?>)recipes.createdAt);
                        case "views":
                            return new OrderSpecifier<>(direction, (ComparableExpressionBase<?>)recipes.views);
                        default:
                            return new OrderSpecifier<>(direction, (ComparableExpressionBase<?>)recipes.id);
                    }
                })
                .toList();
    }
    /**
     * 레시피 수정
     * **/
    @Override
    public Long updateRecipe(Integer recipesId, RecipesRequestDto recipesRequestDto){
        return queryFactory
                .update(t)
                .set(t.name, recipesRequestDto.getName())
                .set(t.amountPerMenu, recipesRequestDto.getAmountPerMenu())
                .set(t.unit, recipesRequestDto.getUnit())
                .where(t.id.eq(recipesId))
                .execute();
    }
    /**
     * 레시피 삭제
     * **/
    @Override
    public Long deleteRecipe(Integer recipesId) {
        return queryFactory
                .delete(t)
                .where(t.id.eq(recipesId))
                .execute();
    }

}
