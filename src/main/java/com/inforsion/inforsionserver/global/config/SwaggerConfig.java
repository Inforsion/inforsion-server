package com.inforsion.inforsionserver.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("로컬 개발 서버"),
                        new Server().url("https://api.inforsion.com").description("운영 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Inforsion API")
                .description("""
                        Inforsion 가게 관리 시스템 API 문서

                        ## 주요 기능
                        - 🔐 JWT 기반 인증/인가 (Redis 토큰 관리)
                        - 👤 회원 관리 (Redis 캐싱)
                        - 🏪 가게 관리
                        - 💰 매출 관리 (커스텀 필드 지원)
                        - 📦 재고 관리
                        - 🛒 상품 관리

                        ## 인증 방법
                        1. POST /api/v1/auth/login 으로 로그인
                        2. 응답으로 받은 accessToken 복사
                        3. 우측 상단 'Authorize' 버튼 클릭
                        4. 'Bearer {accessToken}' 형식으로 입력
                        """)
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Inforsion Team")
                        .email("contact@inforsion.com")
                        .url("https://github.com/Inforsion/inforsion-server")
                )
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                );
    }
}