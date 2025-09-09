package com.inforsion.inforsionserver.domain.recipe.repository;

import com.inforsion.inforsionserver.domain.recipe.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Integer>, RecipeRepositoryCustom {

    List<RecipeEntity> findByMenuIdAndIsActive(Integer menuId, Boolean isActive);

    List<RecipeEntity> findByInventoryIdAndIsActive(Integer inventoryId, Boolean isActive);

    List<RecipeEntity> findByMenuId(Integer menuId);

    List<RecipeEntity> findByInventoryId(Integer inventoryId);

    @Query("SELECT r FROM RecipeEntity r WHERE r.menu.store.id = :storeId AND r.isActive = true")
    List<RecipeEntity> findActiveByStoreId(@Param("storeId") Integer storeId);

    @Query("SELECT r FROM RecipeEntity r WHERE r.menu.id = :menuId AND r.inventory.id = :inventoryId")
    List<RecipeEntity> findByMenuIdAndInventoryId(@Param("menuId") Integer menuId, @Param("inventoryId") Integer inventoryId);

    @Query("SELECT r FROM RecipeEntity r WHERE r.menu.store.id = :storeId")
    List<RecipeEntity> findByStoreId(@Param("storeId") Integer storeId);
}