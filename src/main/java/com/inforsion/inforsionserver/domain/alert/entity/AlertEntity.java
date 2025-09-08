package com.inforsion.inforsionserver.domain.alert.entity;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.global.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Integer alertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryEntity inventory;

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}