package com.inforsion.inforsionserver.domain.user.entity;

import com.inforsion.inforsionserver.global.enums.SocialProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_logins")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SocialLoginEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_id")
    private Integer socialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private SocialProvider provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @Column(name = "provider_email", nullable = false)
    private String providerEmail;

    @Column(name = "last_login_at", nullable = false)
    private LocalDateTime lastLoginAt;

    @Column(name = "is_connected", nullable = false)
    private Boolean isConnected = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void disconnect() {
        this.isConnected = false;
    }
}