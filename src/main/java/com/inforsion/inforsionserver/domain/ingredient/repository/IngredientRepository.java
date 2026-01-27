package com.inforsion.inforsionserver.domain.ingredient.repository;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Integer>, IngredientRepositoryCustom {

    List<IngredientEntity> findByStoreIdAndIsActive(Integer storeId, Boolean isActive);

    Optional<IngredientEntity> findByStoreIdAndName(Integer storeId, String name);
}