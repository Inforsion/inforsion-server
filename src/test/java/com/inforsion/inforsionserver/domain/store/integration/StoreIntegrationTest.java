package com.inforsion.inforsionserver.domain.store.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Store 통합 테스트")
class StoreIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;
    private Integer userId;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        storeRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트용 사용자 생성
        testUser = UserEntity.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        
        testUser = userRepository.saveAndFlush(testUser);
        userId = testUser.getId();
    }

    @Test
    @WithMockUser
    @DisplayName("가게 생성 통합 테스트")
    void createStoreIntegrationTest() throws Exception {
        // given
        String requestJson = """
            {
                "name": "통합테스트 가게",
                "location": "서울시 강남구 테헤란로 123",
                "description": "통합 테스트용 가게입니다",
                "phoneNumber": "010-1234-5678",
                "email": "integration@example.com",
                "businessRegistrationNumber": "123-45-67890",
                "openingHours": "{\\"mon\\": \\"09:00-18:00\\", \\"tue\\": \\"09:00-18:00\\"}"
            }
        """;

        // when & then
        mockMvc.perform(post("/api/v1/stores/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("통합테스트 가게"))
                .andExpect(jsonPath("$.location").value("서울시 강남구 테헤란로 123"))
                .andExpect(jsonPath("$.description").value("통합 테스트용 가게입니다"))
                .andExpect(jsonPath("$.phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.businessRegistrationNumber").value("123-45-67890"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("가게 조회 통합 테스트")
    void getStoreIntegrationTest() throws Exception {
        // given - 가게 먼저 생성
        StoreEntity store = StoreEntity.builder()
                .name("조회용 가게")
                .location("서울시 서초구")
                .description("조회 테스트용 가게")
                .phoneNumber("010-8765-4321")
                .email("query@example.com")
                .isActive(true)
                .user(testUser)
                .build();
        store = storeRepository.saveAndFlush(store);

        // when & then
        mockMvc.perform(get("/api/v1/stores/{storeId}", store.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(store.getId()))
                .andExpect(jsonPath("$.name").value("조회용 가게"))
                .andExpect(jsonPath("$.location").value("서울시 서초구"))
                .andExpect(jsonPath("$.description").value("조회 테스트용 가게"))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    @WithMockUser
    @DisplayName("가게 수정 통합 테스트")
    void updateStoreIntegrationTest() throws Exception {
        // given - 가게 먼저 생성
        StoreEntity store = StoreEntity.builder()
                .name("수정 전 가게")
                .location("수정 전 위치")
                .description("수정 전 설명")
                .isActive(true)
                .user(testUser)
                .build();
        store = storeRepository.saveAndFlush(store);

        String updateJson = """
            {
                "name": "수정된 가게",
                "location": "서울시 강동구",
                "description": "수정된 설명입니다",
                "phoneNumber": "010-9999-8888",
                "email": "updated@example.com",
                "isActive": true
            }
        """;

        // when & then
        mockMvc.perform(put("/api/v1/stores/{storeId}", store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(store.getId()))
                .andExpect(jsonPath("$.name").value("수정된 가게"))
                .andExpect(jsonPath("$.location").value("서울시 강동구"))
                .andExpect(jsonPath("$.description").value("수정된 설명입니다"))
                .andExpect(jsonPath("$.phoneNumber").value("010-9999-8888"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("가게 삭제 통합 테스트")
    void deleteStoreIntegrationTest() throws Exception {
        // given - 가게 먼저 생성
        StoreEntity store = StoreEntity.builder()
                .name("삭제용 가게")
                .location("삭제용 위치")
                .description("삭제 테스트용 가게")
                .isActive(true)
                .user(testUser)
                .build();
        store = storeRepository.saveAndFlush(store);
        Integer storeId = store.getId();

        // when & then - 삭제
        mockMvc.perform(delete("/api/v1/stores/{storeId}", storeId))
                .andDo(print())
                .andExpect(status().isNoContent());

        // then - 삭제 확인
        mockMvc.perform(get("/api/v1/stores/{storeId}", storeId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("존재하지 않는 가게 조회 - 404 에러")
    void getNonExistentStore() throws Exception {
        // given
        Integer nonExistentStoreId = 999;

        // when & then
        mockMvc.perform(get("/api/v1/stores/{storeId}", nonExistentStoreId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("존재하지 않는 사용자로 가게 생성 - 404 에러")
    void createStoreWithNonExistentUser() throws Exception {
        // given
        Integer nonExistentUserId = 999;
        String requestJson = """
            {
                "name": "실패할 가게",
                "location": "서울시 어딘가"
            }
        """;

        // when & then
        mockMvc.perform(post("/api/v1/stores/{userId}", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @Transactional
    @DisplayName("가게 생성 후 데이터베이스 저장 확인")
    void createStoreAndVerifyInDatabase() throws Exception {
        // given
        String requestJson = """
            {
                "name": "DB 확인용 가게",
                "location": "서울시 중구",
                "description": "데이터베이스 저장 확인용"
            }
        """;

        // when
        mockMvc.perform(post("/api/v1/stores/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        // then - 데이터베이스에서 직접 확인
        var stores = storeRepository.findByUserId(userId);
        assertThat(stores).hasSize(1);
        assertThat(stores.get(0).getName()).isEqualTo("DB 확인용 가게");
        assertThat(stores.get(0).getLocation()).isEqualTo("서울시 중구");
        assertThat(stores.get(0).getDescription()).isEqualTo("데이터베이스 저장 확인용");
        assertThat(stores.get(0).getIsActive()).isTrue();
        assertThat(stores.get(0).getUser().getId()).isEqualTo(userId);
    }
}