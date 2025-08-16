package com.inforsion.inforsionserver.user;

import java.util.ArrayList;
import java.util.List;

import com.inforsion.inforsionserver.Store.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 기본키 필드
	
	@Column(unique = true)
	private String email;
	
	private String name;
	private String password;
	
	// 일대다 관계 설정 (유저가 여러 개의 상점을 가질 수 있음)
	@OneToMany(mappedBy = "user")
	private List<Store> stores = new ArrayList<>();
}
