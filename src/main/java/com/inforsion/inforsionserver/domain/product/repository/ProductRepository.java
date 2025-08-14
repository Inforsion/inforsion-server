package com.inforsion.inforsionserver.domain.product.repository;

import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer>, QuerydslPredicateExecutor<ProductEntity>, ProductRepositoryCustom {
    
    List<ProductEntity> findByStoreId(Integer storeId);
    
    List<ProductEntity> findByStoreIdAndInStock(Integer storeId, Boolean inStock);
    
    List<ProductEntity> findByCategory(String category);
    
    List<ProductEntity> findByIsSignature(Boolean isSignature);
    
    Optional<ProductEntity> findByIdAndStoreId(Integer id, Integer storeId);
    
    boolean existsByNameAndStoreId(String name, Integer storeId);
}