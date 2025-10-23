package com.inforsion.inforsionserver.global.infra.kakao;

import com.inforsion.inforsionserver.global.config.KakaoMapConfig;
import com.inforsion.inforsionserver.global.error.code.ErrorCode;
import com.inforsion.inforsionserver.global.error.exception.ExternalApiException;
import com.inforsion.inforsionserver.global.infra.kakao.dto.KakaoAddressSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoMapClient {

    private final KakaoMapConfig kakaoMapConfig;
    private final WebClient webClient;

    /**
     * 카카오 주소 검색 API 호출
     *
     * @param query 검색어
     * @param page  결과 페이지 (1부터 시작)
     * @param size  페이지 당 검색 결과 수
     * @return 카카오 주소 검색 응답
     */
    public KakaoAddressSearchResponse searchAddress(String query, Integer page, Integer size) {
        if (!StringUtils.hasText(query)) {
            throw new IllegalArgumentException("주소 검색어는 필수입니다.");
        }
        if (!StringUtils.hasText(kakaoMapConfig.getRestApiKey())) {
            throw new IllegalStateException("카카오 REST API 키가 설정되지 않았습니다.");
        }
        if (!StringUtils.hasText(kakaoMapConfig.getAddressSearchUrl())) {
            throw new IllegalStateException("카카오 주소 검색 API URL이 설정되지 않았습니다.");
        }

        int resolvedPage = (page == null || page < 1) ? 1 : page;
        int resolvedSize = (size == null || size < 1) ? kakaoMapConfig.getDefaultSize() : size;

        String targetUrl = UriComponentsBuilder
                .fromHttpUrl(kakaoMapConfig.getAddressSearchUrl())
                .queryParam("query", query.trim())
                .queryParam("page", resolvedPage)
                .queryParam("size", resolvedSize)
                .build(true)
                .toUriString();

        log.debug("카카오 주소 검색 API 호출: url={}", targetUrl);

        try {
            return webClient.get()
                    .uri(targetUrl)
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoMapConfig.getRestApiKey().trim())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> status.value() == 401,
                            response -> Mono.error(new ExternalApiException(
                                    ErrorCode.EXTERNAL_API_UNAUTHORIZED,
                                    "카카오 주소 검색 API 인증에 실패했습니다."
                            )))
                    .onStatus(HttpStatusCode::isError,
                            response -> Mono.error(new ExternalApiException(
                                    ErrorCode.EXTERNAL_API_ERROR,
                                    "카카오 주소 검색 API 호출에 실패했습니다. 상태 코드: " + response.statusCode()
                            )))
                    .bodyToMono(KakaoAddressSearchResponse.class)
                    .block();
        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("카카오 주소 검색 API 호출 중 예외 발생", e);
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR,
                    "카카오 주소 검색 API 호출 중 오류가 발생했습니다.", e);
        }
    }
}
