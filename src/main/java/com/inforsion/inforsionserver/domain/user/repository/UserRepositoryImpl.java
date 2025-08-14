package com.inforsion.inforsionserver.domain.user.repository;

import com.inforsion.inforsionserver.domain.user.entity.QUserEntity;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.store.entity.QStoreEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Optional<UserEntity> findByEmailOrUsername(String email, String username) {
        QUserEntity user = QUserEntity.userEntity;
        
        UserEntity result = queryFactory
                .selectFrom(user)
                .where(user.email.eq(email).or(user.username.eq(username)))
                .fetchOne();
        
        return Optional.ofNullable(result);
    }
    
    @Override
    public Long countActiveStoresByUserId(Integer userId) {
        QUserEntity user = QUserEntity.userEntity;
        QStoreEntity store = QStoreEntity.storeEntity;
        
        return queryFactory
                .select(store.count())
                .from(user)
                .join(user.stores, store)
                .where(user.id.eq(userId)
                        .and(store.isActive.eq(true)))
                .fetchOne();
    }
    
    @Override
    public List<UserEntity> findUsersWithActiveStores() {
        QUserEntity user = QUserEntity.userEntity;
        QStoreEntity store = QStoreEntity.storeEntity;
        
        return queryFactory
                .selectFrom(user)
                .join(user.stores, store)
                .where(store.isActive.eq(true))
                .distinct()
                .fetch();
    }
}