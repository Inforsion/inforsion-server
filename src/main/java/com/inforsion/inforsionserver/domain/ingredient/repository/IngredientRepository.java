package com.inforsion.inforsionserver.domain.ingredient.repository;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Integer>, QuerydslPredicateExecutor<IngredientEntity>, IngredientRepositoryCustom {
    
    List<IngredientEntity> findByProductId(Integer productId);
    
    List<IngredientEntity> findByInventoryId(Integer inventoryId);
    
    boolean existsByProductIdAndInventoryId(Integer productId, Integer inventoryId);
}