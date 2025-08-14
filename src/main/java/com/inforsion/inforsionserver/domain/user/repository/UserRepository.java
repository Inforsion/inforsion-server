package com.inforsion.inforsionserver.domain.user.repository;

import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>, QuerydslPredicateExecutor<UserEntity>, UserRepositoryCustom {

    Optional<UserEntity> findByEmail(String email);
    
    Optional<UserEntity> findByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
}