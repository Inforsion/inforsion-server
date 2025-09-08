package com.inforsion.inforsionserver.domain.analytics.entity;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AnalyticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analystic_id")
    private Integer analyticsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @Column(name = "analysis_date")
    private LocalDate analysisDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    private PeriodType periodType;

    @Column(name = "ingredient_name")
    private String ingredientName;

    @Column(name = "total_consumed", precision = 10, scale = 2)
    private BigDecimal totalConsumed;

    @Column(name = "total_restocked", precision = 10, scale = 2)
    private BigDecimal totalRestocked;

    @Column(name = "average_daily_usage", precision = 10, scale = 2)
    private BigDecimal averageDailyUsage;

    @Column(name = "waste_amount", precision = 10, scale = 2)
    private BigDecimal wasteAmount;

    @Column(name = "restock_frequency")
    private Integer restockFrequency;

    @Column(name = "inventory_turnover", precision = 10, scale = 2)
    private BigDecimal inventoryTurnover;

    @Column(name = "popular_menu")
    private String popularMenu;

    @Column(name = "usage_trend", columnDefinition = "JSON")
    private String usageTrend;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}