package com.inforsion.inforsionserver.Store;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.inforsion.inforsionserver.Inventory.Inventory;
import com.inforsion.inforsionserver.Sales.Sales;
import com.inforsion.inforsionserver.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "stores")
public class Store {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 가게 이름

    private String location; // 위치

    @Column(length = 1000)
    private String description; // 설명

    private String phoneNumber; // 전화번호

    private String email; // 이메일

    /**
     * openingHours는 JSON 형태로 저장 가능
     * MySQL에서는 TEXT 컬럼으로 저장
     */
    @Lob
    private String openingHours; // 영업시간(JSON 문자열)

    private boolean isActive; // 활성 여부

    private LocalDateTime createdAt; // 생성일
    private LocalDateTime updatedAt; // 수정일

    // 데이터 저장 전 생성/수정 날짜 자동 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Store 도메인은 User 도메인과 Sales 도메인 리스트를 갖게 된다.
    // user는 여러 개의 상점을 가질 수 있고 상점마다 매출이 다름으로 ,,
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "store")
    private List<Inventory> inventory = new ArrayList<>();
    
    @OneToMany(mappedBy = "store")
    private List<Sales> sales = new ArrayList<>();
}
