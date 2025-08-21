package com.inforsion.inforsionserver.domain.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inforsion.inforsionserver.domain.store.dto.StoreDto;
import com.inforsion.inforsionserver.domain.store.service.StoreService;
import com.inforsion.inforsionserver.global.error.exception.StoreNotFoundException;
import com.inforsion.inforsionserver.global.error.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StoreController.class,
           excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@DisplayName("StoreController 테스트")
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("가게 생성 성공")
    void createStore_Success() throws Exception {
        // given
        Integer userId = 1;
        StoreDto.CreateRequest request = new StoreDto.CreateRequest();
        // request 필드 설정을 위해 reflection 사용이나 builder 패턴이 필요하지만,
        // 테스트를 위해 JSON으로 직접 작성
        String requestJson = """
            {
                "name": "테스트 가게",
                "location": "서울시 강남구",
                "description": "테스트용 가게입니다",
                "phoneNumber": "010-1234-5678",
                "email": "test@example.com",
                "businessRegistrationNumber": "123-45-67890",
                "openingHours": "{\\"mon\\": \\"09:00-18:00\\"}"
            }
        """;

        StoreDto.Response response = StoreDto.Response.builder()
                .id(1)
                .name("테스트 가게")
                .location("서울시 강남구")
                .description("테스트용 가게입니다")
                .phoneNumber("010-1234-5678")
                .email("test@example.com")
                .businessRegistrationNumber("123-45-67890")
                .openingHours("{\"mon\": \"09:00-18:00\"}")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .userId(userId)
                .build();

        given(storeService.createStore(eq(userId), any(StoreDto.CreateRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/stores/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("테스트 가게"))
                .andExpect(jsonPath("$.location").value("서울시 강남구"))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    @DisplayName("가게 생성 실패 - 존재하지 않는 사용자")
    void createStore_UserNotFound() throws Exception {
        // given
        Integer userId = 999;
        String requestJson = """
            {
                "name": "테스트 가게",
                "location": "서울시 강남구"
            }
        """;

        given(storeService.createStore(eq(userId), any(StoreDto.CreateRequest.class)))
                .willThrow(new UserNotFoundException());

        // when & then
        mockMvc.perform(post("/api/v1/stores/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("가게 조회 성공")
    void getStore_Success() throws Exception {
        // given
        Integer storeId = 1;
        StoreDto.Response response = StoreDto.Response.builder()
                .id(storeId)
                .name("테스트 가게")
                .location("서울시 강남구")
                .description("테스트용 가게입니다")
                .isActive(true)
                .userId(1)
                .build();

        given(storeService.getStore(storeId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/stores/{storeId}", storeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(storeId))
                .andExpect(jsonPath("$.name").value("테스트 가게"))
                .andExpect(jsonPath("$.location").value("서울시 강남구"));
    }

    @Test
    @DisplayName("가게 조회 실패 - 존재하지 않는 가게")
    void getStore_NotFound() throws Exception {
        // given
        Integer storeId = 999;
        given(storeService.getStore(storeId)).willThrow(new StoreNotFoundException());

        // when & then
        mockMvc.perform(get("/api/v1/stores/{storeId}", storeId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("가게 수정 성공")
    void updateStore_Success() throws Exception {
        // given
        Integer storeId = 1;
        String requestJson = """
            {
                "name": "수정된 가게",
                "location": "서울시 서초구",
                "description": "수정된 가게입니다",
                "isActive": true
            }
        """;

        StoreDto.Response response = StoreDto.Response.builder()
                .id(storeId)
                .name("수정된 가게")
                .location("서울시 서초구")
                .description("수정된 가게입니다")
                .isActive(true)
                .userId(1)
                .build();

        given(storeService.updateStore(eq(storeId), any(StoreDto.UpdateRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/stores/{storeId}", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(storeId))
                .andExpect(jsonPath("$.name").value("수정된 가게"));
    }

    @Test
    @DisplayName("가게 삭제 성공")
    void deleteStore_Success() throws Exception {
        // given
        Integer storeId = 1;
        doNothing().when(storeService).deleteStore(storeId);

        // when & then
        mockMvc.perform(delete("/api/v1/stores/{storeId}", storeId)
                        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("가게 삭제 실패 - 존재하지 않는 가게")
    void deleteStore_NotFound() throws Exception {
        // given
        Integer storeId = 999;
        doThrow(new StoreNotFoundException()).when(storeService).deleteStore(storeId);

        // when & then
        mockMvc.perform(delete("/api/v1/stores/{storeId}", storeId)
                        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자별 가게 목록 조회 성공 - 전체 조회")
    void getStoresByUserId_Success() throws Exception {
        // given
        Integer userId = 1;
        List<StoreDto.Response> responses = Arrays.asList(
                StoreDto.Response.builder()
                        .id(1)
                        .name("첫 번째 가게")
                        .location("서울시 강남구")
                        .description("첫 번째 테스트 가게")
                        .isActive(true)
                        .userId(userId)
                        .build(),
                StoreDto.Response.builder()
                        .id(2)
                        .name("두 번째 가게")
                        .location("서울시 서초구")
                        .description("두 번째 테스트 가게")
                        .isActive(false)
                        .userId(userId)
                        .build()
        );

        given(storeService.getStoresByUserId(userId)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/stores/user/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("첫 번째 가게"))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("두 번째 가게"))
                .andExpect(jsonPath("$[1].isActive").value(false));
    }

    @Test
    @DisplayName("사용자별 가게 목록 조회 성공 - 활성 가게만")
    void getStoresByUserId_ActiveOnly() throws Exception {
        // given
        Integer userId = 1;
        Boolean isActive = true;
        List<StoreDto.Response> responses = Arrays.asList(
                StoreDto.Response.builder()
                        .id(1)
                        .name("활성 가게")
                        .location("서울시 강남구")
                        .description("활성 상태의 가게")
                        .isActive(true)
                        .userId(userId)
                        .build()
        );

        given(storeService.getStoresByUserIdAndStatus(userId, isActive)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/stores/user/{userId}", userId)
                        .param("isActive", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("활성 가게"))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    @DisplayName("사용자별 가게 목록 조회 성공 - 비활성 가게만")
    void getStoresByUserId_InactiveOnly() throws Exception {
        // given
        Integer userId = 1;
        Boolean isActive = false;
        List<StoreDto.Response> responses = Arrays.asList(
                StoreDto.Response.builder()
                        .id(2)
                        .name("비활성 가게")
                        .location("서울시 마포구")
                        .description("비활성 상태의 가게")
                        .isActive(false)
                        .userId(userId)
                        .build()
        );

        given(storeService.getStoresByUserIdAndStatus(userId, isActive)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/stores/user/{userId}", userId)
                        .param("isActive", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("비활성 가게"))
                .andExpect(jsonPath("$[0].isActive").value(false));
    }

    @Test
    @DisplayName("사용자별 가게 목록 조회 성공 - 빈 목록")
    void getStoresByUserId_EmptyList() throws Exception {
        // given
        Integer userId = 1;
        List<StoreDto.Response> responses = Arrays.asList();

        given(storeService.getStoresByUserId(userId)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/stores/user/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("사용자별 가게 목록 조회 실패 - 존재하지 않는 사용자")
    void getStoresByUserId_UserNotFound() throws Exception {
        // given
        Integer userId = 999;
        given(storeService.getStoresByUserId(userId)).willThrow(new UserNotFoundException());

        // when & then
        mockMvc.perform(get("/api/v1/stores/user/{userId}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}