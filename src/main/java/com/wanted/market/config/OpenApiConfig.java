package com.wanted.market.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Wanted Market API")
                .version("v1.0")
                .description("Wanted Market REST API 명세서\n\n" +
                        "## 인증\n" +
                        "- 대부분의 API는 JWT 토큰 인증이 필요합니다.\n" +
                        "- Authorization 헤더에 'Bearer {token}' 형식으로 토큰을 전달해야 합니다.\n\n" +
                        "## 에러 응답\n" +
                        "- 400: 잘못된 요청\n" +
                        "- 401: 인증 실패\n" +
                        "- 403: 권한 없음\n" +
                        "- 404: 리소스를 찾을 수 없음\n" +
                        "- 409: 충돌 (중복된 데이터 등)\n" +
                        "- 500: 서버 에러")
                .contact(new Contact()
                        .name("API Support")
                        .email("support@wantedmarket.com"));

        // JWT 인증 설정
        String jwtSchemeName = "JWT Authorization";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT 토큰을 입력해주세요. (Bearer 키워드는 자동으로 추가됩니다.)"));

        // 서버 정보 추가
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        return new OpenAPI()
                .info(info)
                .addServersItem(localServer)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
