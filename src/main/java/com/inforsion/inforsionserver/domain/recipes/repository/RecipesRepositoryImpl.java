package com.inforsion.inforsionserver.domain.recipes.repository;

import com.inforsion.inforsionserver.domain.recipes.entity.QRecipesEntity;
import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecipesRepositoryImpl implements RecipesRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QRecipesEntity qRecipes = QRecipesEntity.recipesEntity;

    @Override
    public Page<RecipesEntity> findRecipesByStoreIdWithPaging(Integer storeId, Pageable pageable) {
        List<RecipesEntity> recipes = queryFactory
                .selectFrom(qRecipes)
                .where(qRecipes.store.id.eq(storeId))
                .orderBy(qRecipes.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qRecipes.count())
                .from(qRecipes)
                .where(qRecipes.store.id.eq(storeId))
                .fetchOne();

        return new PageImpl<>(recipes, pageable, total != null ? total : 0);
    }

    @Override
    public Page<RecipesEntity> findActiveRecipesByStoreIdWithPaging(Integer storeId, Pageable pageable) {
        List<RecipesEntity> recipes = queryFactory
                .selectFrom(qRecipes)
                .where(qRecipes.store.id.eq(storeId)
                        .and(qRecipes.isActive.eq(true)))
                .orderBy(qRecipes.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qRecipes.count())
                .from(qRecipes)
                .where(qRecipes.store.id.eq(storeId)
                        .and(qRecipes.isActive.eq(true)))
                .fetchOne();

        return new PageImpl<>(recipes, pageable, total != null ? total : 0);
    }

    @Override
    public List<RecipesEntity> findRecipesByMenuIdWithIngredientDetails(Integer menuId) {
        return queryFactory
                .selectFrom(qRecipes)
                .leftJoin(qRecipes.inventory).fetchJoin()
                .where(qRecipes.menu.id.eq(menuId)
                        .and(qRecipes.isActive.eq(true)))
                .fetch();
    }

    @Override
    public List<RecipesEntity> findRecipesUsingInventoryId(Integer inventoryId) {
        return queryFactory
                .selectFrom(qRecipes)
                .leftJoin(qRecipes.menu).fetchJoin()
                .where(qRecipes.inventory.id.eq(inventoryId)
                        .and(qRecipes.isActive.eq(true)))
                .fetch();
    }

    @Override
    public List<RecipesEntity> findRecipesByStoreAndIngredientName(Integer storeId, String ingredientName) {
        return queryFactory
                .selectFrom(qRecipes)
                .leftJoin(qRecipes.inventory).fetchJoin()
                .leftJoin(qRecipes.menu).fetchJoin()
                .where(qRecipes.store.id.eq(storeId)
                        .and(qRecipes.inventory.name.containsIgnoreCase(ingredientName))
                        .and(qRecipes.isActive.eq(true)))
                .fetch();
    }

    @Override
    public void deactivateRecipesByMenuId(Integer menuId) {
        queryFactory
                .update(qRecipes)
                .set(qRecipes.isActive, false)
                .where(qRecipes.menu.id.eq(menuId))
                .execute();
    }

    @Override
    public void deactivateRecipesByInventoryId(Integer inventoryId) {
        queryFactory
                .update(qRecipes)
                .set(qRecipes.isActive, false)
                .where(qRecipes.inventory.id.eq(inventoryId))
                .execute();
    }
}
