package com.inforsion.inforsionserver.domain.ingredient_dev.repository;

import com.inforsion.inforsionserver.domain.ingredient_dev.entity.IngredientDevEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientDevRepository extends JpaRepository<IngredientDevEntity, Integer>, IngredientDevRepositoryCustom {
}
