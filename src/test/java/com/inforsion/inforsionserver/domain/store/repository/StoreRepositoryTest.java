package com.inforsion.inforsionserver.domain.store.repository;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import com.inforsion.inforsionserver.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({StoreRepositoryImpl.class, QueryDslConfig.class})
@DisplayName("StoreRepository 테스트")
class StoreRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;
    private StoreEntity activeStore;
    private StoreEntity inactiveStore;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = UserEntity.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        userRepository.save(testUser);

        // 활성 가게 생성
        activeStore = StoreEntity.builder()
                .name("활성 가게")
                .location("서울시 강남구 테헤란로")
                .description("활성 상태의 테스트 가게입니다")
                .phoneNumber("010-1234-5678")
                .email("active@example.com")
                .businessRegistrationNumber("123-45-67890")
                .openingHours("{\"mon\": \"09:00-18:00\"}")
                .isActive(true)
                .user(testUser)
                .build();
        storeRepository.save(activeStore);

        // 비활성 가게 생성
        inactiveStore = StoreEntity.builder()
                .name("비활성 가게")
                .location("서울시 서초구 반포대로")
                .description("비활성 상태의 테스트 가게입니다")
                .phoneNumber("010-8765-4321")
                .email("inactive@example.com")
                .businessRegistrationNumber("111-22-33333")
                .openingHours("{\"tue\": \"10:00-20:00\"}")
                .isActive(false)
                .user(testUser)
                .build();
        storeRepository.save(inactiveStore);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("가게 생성 및 조회")
    void saveAndFindStore() {
        // given
        UserEntity newUser = UserEntity.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .build();
        userRepository.save(newUser);

        StoreEntity newStore = StoreEntity.builder()
                .name("새로운 가게")
                .location("부산시 해운대구")
                .description("새로 생성된 가게입니다")
                .isActive(true)
                .user(newUser)
                .build();

        // when
        StoreEntity savedStore = storeRepository.save(newStore);

        // then
        assertThat(savedStore).isNotNull();
        assertThat(savedStore.getId()).isNotNull();

        Optional<StoreEntity> foundStore = storeRepository.findById(savedStore.getId());
        assertThat(foundStore).isPresent();
        assertThat(foundStore.get().getName()).isEqualTo("새로운 가게");
        assertThat(foundStore.get().getLocation()).isEqualTo("부산시 해운대구");
    }

    @Test
    @DisplayName("지역별 활성 가게 페이징 조회")
    void findActiveStoresByLocation() {
        // given
        String location = "강남구";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<StoreEntity> result = storeRepository.findActiveStoresByLocation(location, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("활성 가게");
        assertThat(result.getContent().get(0).getLocation()).contains("강남구");
        assertThat(result.getContent().get(0).getIsActive()).isTrue();
    }

    @Test
    @DisplayName("지역별 활성 가게 페이징 조회 - 결과 없음")
    void findActiveStoresByLocation_NoResults() {
        // given
        String location = "제주도";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<StoreEntity> result = storeRepository.findActiveStoresByLocation(location, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자별 활성 가게 개수 조회")
    void countActiveStoresByUserId() {
        // when
        Long count = storeRepository.countActiveStoresByUserId(testUser.getId());

        // then
        assertThat(count).isEqualTo(1L); // 활성 가게 1개만 카운트
    }

    @Test
    @DisplayName("키워드로 가게 검색 - 가게명으로 검색")
    void searchStoresByKeyword_ByName() {
        // given
        String keyword = "활성";

        // when
        List<StoreEntity> result = storeRepository.searchStoresByKeyword(keyword);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("활성");
        assertThat(result.get(0).getIsActive()).isTrue();
    }

    @Test
    @DisplayName("키워드로 가게 검색 - 지역명으로 검색")
    void searchStoresByKeyword_ByLocation() {
        // given
        String keyword = "테헤란로";

        // when
        List<StoreEntity> result = storeRepository.searchStoresByKeyword(keyword);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLocation()).contains("테헤란로");
        assertThat(result.get(0).getIsActive()).isTrue();
    }

    @Test
    @DisplayName("키워드로 가게 검색 - 설명으로 검색")
    void searchStoresByKeyword_ByDescription() {
        // given
        String keyword = "활성 상태";

        // when
        List<StoreEntity> result = storeRepository.searchStoresByKeyword(keyword);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).contains("활성 상태");
        assertThat(result.get(0).getIsActive()).isTrue();
    }

    @Test
    @DisplayName("키워드로 가게 검색 - 대소문자 구분 없이")
    void searchStoresByKeyword_CaseInsensitive() {
        // given
        String keyword = "활성"; // 한글로 검색

        // when
        List<StoreEntity> result = storeRepository.searchStoresByKeyword(keyword);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).containsIgnoringCase("활성");
    }

    @Test
    @DisplayName("키워드로 가게 검색 - 결과 없음")
    void searchStoresByKeyword_NoResults() {
        // given
        String keyword = "존재하지않는키워드";

        // when
        List<StoreEntity> result = storeRepository.searchStoresByKeyword(keyword);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("가게 삭제")
    void deleteStore() {
        // given
        Integer storeId = activeStore.getId();

        // when
        storeRepository.deleteById(storeId);

        // then
        Optional<StoreEntity> deletedStore = storeRepository.findById(storeId);
        assertThat(deletedStore).isEmpty();
    }

    @Test
    @DisplayName("사용자별 가게 조회")
    void findByUserId() {
        // when
        List<StoreEntity> stores = storeRepository.findByUserId(testUser.getId());

        // then
        assertThat(stores).hasSize(2); // 활성, 비활성 가게 모두 포함
        assertThat(stores).extracting(StoreEntity::getName)
                .containsExactlyInAnyOrder("활성 가게", "비활성 가게");
    }

    @Test
    @DisplayName("사용자별 활성 가게만 조회")
    void findByUserIdAndIsActive() {
        // when
        List<StoreEntity> activeStores = storeRepository.findByUserIdAndIsActive(testUser.getId(), true);
        List<StoreEntity> inactiveStores = storeRepository.findByUserIdAndIsActive(testUser.getId(), false);

        // then
        assertThat(activeStores).hasSize(1);
        assertThat(activeStores.get(0).getName()).isEqualTo("활성 가게");
        
        assertThat(inactiveStores).hasSize(1);
        assertThat(inactiveStores.get(0).getName()).isEqualTo("비활성 가게");
    }

    @Test
    @DisplayName("가게명과 위치로 중복 체크")
    void existsByNameAndLocation() {
        // when
        boolean exists = storeRepository.existsByNameAndLocation("활성 가게", "서울시 강남구 테헤란로");
        boolean notExists = storeRepository.existsByNameAndLocation("없는 가게", "없는 위치");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}