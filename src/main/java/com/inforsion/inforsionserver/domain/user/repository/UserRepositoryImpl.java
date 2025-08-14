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
    
    /**
     * 이메일 또는 사용자명으로 사용자 조회
     * 
     * QueryDSL을 사용하여 OR 조건으로 사용자를 검색합니다.
     * 로그인 시 이메일/사용자명 중 하나로 인증할 때 사용됩니다.
     * 
     * @param email 검색할 이메일
     * @param username 검색할 사용자명
     * @return 조건에 맞는 사용자 (없으면 Optional.empty())
     */
    @Override
    public Optional<UserEntity> findByEmailOrUsername(String email, String username) {
        QUserEntity user = QUserEntity.userEntity;
        
        UserEntity result = queryFactory
                .selectFrom(user)
                .where(user.email.eq(email).or(user.username.eq(username)))
                .fetchOne();
        
        return Optional.ofNullable(result);
    }
    
    /**
     * 특정 사용자의 활성 상태 가게 개수 조회
     * 
     * User와 Store 엔티티를 조인하여 활성 상태인 가게의 개수를 집계합니다.
     * 사용자 대시보드에서 운영 중인 가게 수를 표시할 때 사용됩니다.
     * 
     * @param userId 사용자 ID
     * @return 활성 상태인 가게 개수
     */
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
    
    /**
     * 활성 가게를 보유한 사용자 목록 조회
     * 
     * User와 Store를 조인하여 현재 운영 중인 가게가 있는 사용자만 조회합니다.
     * distinct를 사용하여 중복된 사용자를 제거합니다.
     * 관리자 페이지에서 활성 사용자 목록을 확인할 때 사용됩니다.
     * 
     * @return 활성 가게를 보유한 사용자 목록
     */
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