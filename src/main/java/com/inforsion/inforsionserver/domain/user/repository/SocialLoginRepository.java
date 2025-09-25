package com.inforsion.inforsionserver.domain.user.repository;

import com.inforsion.inforsionserver.domain.user.entity.SocialLoginEntity;
import com.inforsion.inforsionserver.global.enums.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialLoginRepository extends JpaRepository<SocialLoginEntity, Integer> {

    Optional<SocialLoginEntity> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);

    List<SocialLoginEntity> findByUserId(Integer userId);

    List<SocialLoginEntity> findByUserIdAndIsConnected(Integer userId, Boolean isConnected);

    Optional<SocialLoginEntity> findByUserIdAndProvider(Integer userId, SocialProvider provider);

    @Query("SELECT sl FROM SocialLoginEntity sl WHERE sl.provider = :provider AND sl.providerEmail = :email AND sl.isConnected = true")
    Optional<SocialLoginEntity> findByProviderAndEmailAndConnected(@Param("provider") SocialProvider provider, 
                                                                  @Param("email") String email);

    @Query("SELECT COUNT(sl) FROM SocialLoginEntity sl WHERE sl.user.id = :userId AND sl.isConnected = true")
    Long countConnectedByUserId(@Param("userId") Integer userId);
}