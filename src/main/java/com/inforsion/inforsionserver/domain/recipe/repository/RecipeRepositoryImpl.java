package com.inforsion.inforsionserver.domain.recipe.repository;

import com.inforsion.inforsionserver.domain.recipe.entity.QRecipeEntity;
import com.inforsion.inforsionserver.domain.recipe.entity.RecipeEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QRecipeEntity qRecipe = QRecipeEntity.recipeEntity;

    @Override
    public Page<RecipeEntity> findRecipesByStoreIdWithPaging(Integer storeId, Pageable pageable) {
        List<RecipeEntity> recipes = queryFactory
                .selectFrom(qRecipe)
                .where(qRecipe.menu.store.id.eq(storeId))
                .orderBy(qRecipe.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qRecipe.count())
                .from(qRecipe)
                .where(qRecipe.menu.store.id.eq(storeId))
                .fetchOne();

        return new PageImpl<>(recipes, pageable, total != null ? total : 0);
    }

    @Override
    public Page<RecipeEntity> findActiveRecipesByStoreIdWithPaging(Integer storeId, Pageable pageable) {
        List<RecipeEntity> recipes = queryFactory
                .selectFrom(qRecipe)
                .where(qRecipe.menu.store.id.eq(storeId)
                        .and(qRecipe.isActive.eq(true)))
                .orderBy(qRecipe.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qRecipe.count())
                .from(qRecipe)
                .where(qRecipe.menu.store.id.eq(storeId)
                        .and(qRecipe.isActive.eq(true)))
                .fetchOne();

        return new PageImpl<>(recipes, pageable, total != null ? total : 0);
    }

    @Override
    public List<RecipeEntity> findRecipesByMenuIdWithIngredientDetails(Integer menuId) {
        return queryFactory
                .selectFrom(qRecipe)
                .leftJoin(qRecipe.inventory).fetchJoin()
                .where(qRecipe.menu.id.eq(menuId)
                        .and(qRecipe.isActive.eq(true)))
                .fetch();
    }

    @Override
    public List<RecipeEntity> findRecipesUsingInventoryId(Integer inventoryId) {
        return queryFactory
                .selectFrom(qRecipe)
                .leftJoin(qRecipe.menu).fetchJoin()
                .where(qRecipe.inventory.id.eq(inventoryId)
                        .and(qRecipe.isActive.eq(true)))
                .fetch();
    }

    @Override
    public List<RecipeEntity> findRecipesByStoreAndIngredientName(Integer storeId, String ingredientName) {
        return queryFactory
                .selectFrom(qRecipe)
                .leftJoin(qRecipe.inventory).fetchJoin()
                .leftJoin(qRecipe.menu).fetchJoin()
                .where(qRecipe.menu.store.id.eq(storeId)
                        .and(qRecipe.inventory.name.containsIgnoreCase(ingredientName))
                        .and(qRecipe.isActive.eq(true)))
                .fetch();
    }

    @Override
    public void deactivateRecipesByMenuId(Integer menuId) {
        queryFactory
                .update(qRecipe)
                .set(qRecipe.isActive, false)
                .where(qRecipe.menu.id.eq(menuId))
                .execute();
    }

    @Override
    public void deactivateRecipesByInventoryId(Integer inventoryId) {
        queryFactory
                .update(qRecipe)
                .set(qRecipe.isActive, false)
                .where(qRecipe.inventory.id.eq(inventoryId))
                .execute();
    }
}