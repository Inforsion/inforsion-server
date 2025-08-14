package com.inforsion.inforsionserver.domain.user.repository;

import com.inforsion.inforsionserver.domain.user.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {
    
    Optional<UserEntity> findByEmailOrUsername(String email, String username);
    
    Long countActiveStoresByUserId(Integer userId);
    
    List<UserEntity> findUsersWithActiveStores();
}