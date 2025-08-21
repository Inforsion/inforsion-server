package com.inforsion.inforsionserver.domain.store.service;

import com.inforsion.inforsionserver.domain.store.dto.StoreDto;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import com.inforsion.inforsionserver.global.error.exception.StoreNotFoundException;
import com.inforsion.inforsionserver.global.error.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreService 테스트")
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StoreService storeService;

    private UserEntity testUser;
    private StoreEntity testStore;
    private StoreDto.CreateRequest createRequest;
    private StoreDto.UpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        testStore = StoreEntity.builder()
                .id(1)
                .name("테스트 가게")
                .location("서울시 강남구")
                .description("테스트용 가게입니다")
                .phoneNumber("010-1234-5678")
                .email("store@example.com")
                .businessRegistrationNumber("123-45-67890")
                .openingHours("{\"mon\": \"09:00-18:00\"}")
                .isActive(true)
                .user(testUser)
                .build();

        // CreateRequest는 Setter가 없으므로 실제 테스트에서는 JSON을 통해 처리하거나
        // Builder 패턴을 추가해야 합니다. 여기서는 Mock 객체로 처리합니다.
        createRequest = new StoreDto.CreateRequest();
        updateRequest = new StoreDto.UpdateRequest();
    }

    @Test
    @DisplayName("가게 생성 성공")
    void createStore_Success() {
        // given
        Integer userId = 1;
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(storeRepository.save(any(StoreEntity.class))).willReturn(testStore);

        // when
        StoreDto.Response response = storeService.createStore(userId, createRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testStore.getId());
        assertThat(response.getName()).isEqualTo(testStore.getName());
        assertThat(response.getLocation()).isEqualTo(testStore.getLocation());
        assertThat(response.getUserId()).isEqualTo(testUser.getId());
        
        verify(userRepository).findById(userId);
        verify(storeRepository).save(any(StoreEntity.class));
    }

    @Test
    @DisplayName("가게 생성 실패 - 존재하지 않는 사용자")
    void createStore_UserNotFound() {
        // given
        Integer userId = 999;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.createStore(userId, createRequest))
                .isInstanceOf(UserNotFoundException.class);
        
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("가게 조회 성공")
    void getStore_Success() {
        // given
        Integer storeId = 1;
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));

        // when
        StoreDto.Response response = storeService.getStore(storeId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testStore.getId());
        assertThat(response.getName()).isEqualTo(testStore.getName());
        assertThat(response.getLocation()).isEqualTo(testStore.getLocation());
        assertThat(response.getUserId()).isEqualTo(testUser.getId());
        
        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("가게 조회 실패 - 존재하지 않는 가게")
    void getStore_NotFound() {
        // given
        Integer storeId = 999;
        given(storeRepository.findById(storeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.getStore(storeId))
                .isInstanceOf(StoreNotFoundException.class);
        
        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("가게 정보 수정 성공")
    void updateStore_Success() {
        // given
        Integer storeId = 1;
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));

        // when
        StoreDto.Response response = storeService.updateStore(storeId, updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testStore.getId());
        
        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("가게 정보 수정 실패 - 존재하지 않는 가게")
    void updateStore_NotFound() {
        // given
        Integer storeId = 999;
        given(storeRepository.findById(storeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.updateStore(storeId, updateRequest))
                .isInstanceOf(StoreNotFoundException.class);
        
        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("가게 삭제 성공")
    void deleteStore_Success() {
        // given
        Integer storeId = 1;
        given(storeRepository.findById(storeId)).willReturn(Optional.of(testStore));

        // when
        storeService.deleteStore(storeId);

        // then
        verify(storeRepository).findById(storeId);
        verify(storeRepository).delete(testStore);
    }

    @Test
    @DisplayName("가게 삭제 실패 - 존재하지 않는 가게")
    void deleteStore_NotFound() {
        // given
        Integer storeId = 999;
        given(storeRepository.findById(storeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.deleteStore(storeId))
                .isInstanceOf(StoreNotFoundException.class);
        
        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("사용자별 가게 목록 조회 성공")
    void getStoresByUserId_Success() {
        // given
        Integer userId = 1;
        StoreEntity store2 = StoreEntity.builder()
                .id(2)
                .name("두 번째 가게")
                .location("서울시 서초구")
                .description("두 번째 테스트용 가게")
                .isActive(true)
                .user(testUser)
                .build();
        
        List<StoreEntity> stores = Arrays.asList(testStore, store2);
        
        given(userRepository.existsById(userId)).willReturn(true);
        given(storeRepository.findByUserId(userId)).willReturn(stores);

        // when
        List<StoreDto.Response> responses = storeService.getStoresByUserId(userId);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(testStore.getId());
        assertThat(responses.get(0).getName()).isEqualTo(testStore.getName());
        assertThat(responses.get(1).getId()).isEqualTo(store2.getId());
        assertThat(responses.get(1).getName()).isEqualTo(store2.getName());
        
        verify(userRepository).existsById(userId);
        verify(storeRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("사용자별 가게 목록 조회 실패 - 존재하지 않는 사용자")
    void getStoresByUserId_UserNotFound() {
        // given
        Integer userId = 999;
        given(userRepository.existsById(userId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> storeService.getStoresByUserId(userId))
                .isInstanceOf(UserNotFoundException.class);
        
        verify(userRepository).existsById(userId);
    }

    @Test
    @DisplayName("사용자별 가게 목록 조회 성공 - 빈 목록")
    void getStoresByUserId_EmptyList() {
        // given
        Integer userId = 1;
        given(userRepository.existsById(userId)).willReturn(true);
        given(storeRepository.findByUserId(userId)).willReturn(Arrays.asList());

        // when
        List<StoreDto.Response> responses = storeService.getStoresByUserId(userId);

        // then
        assertThat(responses).isEmpty();
        
        verify(userRepository).existsById(userId);
        verify(storeRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("사용자별 활성 가게 목록 조회 성공")
    void getStoresByUserIdAndStatus_ActiveStores() {
        // given
        Integer userId = 1;
        Boolean isActive = true;
        
        List<StoreEntity> activeStores = Arrays.asList(testStore);
        
        given(userRepository.existsById(userId)).willReturn(true);
        given(storeRepository.findByUserIdAndIsActive(userId, isActive)).willReturn(activeStores);

        // when
        List<StoreDto.Response> responses = storeService.getStoresByUserIdAndStatus(userId, isActive);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(testStore.getId());
        assertThat(responses.get(0).getIsActive()).isTrue();
        
        verify(userRepository).existsById(userId);
        verify(storeRepository).findByUserIdAndIsActive(userId, isActive);
    }

    @Test
    @DisplayName("사용자별 비활성 가게 목록 조회 성공")
    void getStoresByUserIdAndStatus_InactiveStores() {
        // given
        Integer userId = 1;
        Boolean isActive = false;
        
        StoreEntity inactiveStore = StoreEntity.builder()
                .id(3)
                .name("비활성 가게")
                .location("서울시 마포구")
                .description("비활성 상태의 가게")
                .isActive(false)
                .user(testUser)
                .build();
        
        List<StoreEntity> inactiveStores = Arrays.asList(inactiveStore);
        
        given(userRepository.existsById(userId)).willReturn(true);
        given(storeRepository.findByUserIdAndIsActive(userId, isActive)).willReturn(inactiveStores);

        // when
        List<StoreDto.Response> responses = storeService.getStoresByUserIdAndStatus(userId, isActive);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(inactiveStore.getId());
        assertThat(responses.get(0).getIsActive()).isFalse();
        
        verify(userRepository).existsById(userId);
        verify(storeRepository).findByUserIdAndIsActive(userId, isActive);
    }

    @Test
    @DisplayName("사용자별 활성 상태별 가게 목록 조회 실패 - 존재하지 않는 사용자")
    void getStoresByUserIdAndStatus_UserNotFound() {
        // given
        Integer userId = 999;
        Boolean isActive = true;
        given(userRepository.existsById(userId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> storeService.getStoresByUserIdAndStatus(userId, isActive))
                .isInstanceOf(UserNotFoundException.class);
        
        verify(userRepository).existsById(userId);
    }
}