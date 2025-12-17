package com.inforsion.inforsionserver.domain.recipes.repository;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipesRepository extends JpaRepository<RecipesEntity, Integer>, RecipesRepositoryCustom {

    Page<RecipesEntity> findAllByStoreId(Integer storeId, Pageable pageable);

    List<RecipesEntity> findByMenuIdAndIsActive(Integer menuId, Boolean isActive);

    List<RecipesEntity> findByIngredientIdAndIsActive(Integer ingredientId, Boolean isActive);

    List<RecipesEntity> findByMenuId(Integer menuId);

    List<RecipesEntity> findByIngredientId(Integer ingredientId);

    @Query("SELECT r FROM RecipesEntity r WHERE r.store.id = :storeId AND r.isActive = true")
    List<RecipesEntity> findActiveByStoreId(@Param("storeId") Integer storeId);

    @Query("SELECT r FROM RecipesEntity r WHERE r.menu.id = :menuId AND r.ingredient.id = :ingredientId")
    List<RecipesEntity> findByMenuIdAndIngredientId(@Param("menuId") Integer menuId, @Param("ingredientId") Integer ingredientId);

    @Query("SELECT r FROM RecipesEntity r WHERE r.store.id = :storeId")
    List<RecipesEntity> findByStoreId(@Param("storeId") Integer storeId);

    boolean existsByStoreAndMenuAndIngredient(StoreEntity store, ProductEntity menu, IngredientEntity ingredient);
}
