package com.wanted.market.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Value("${portone.api.base-url}")
    private String portOneBaseUrl;

    @Bean
    public RestClient portOneRestClient() {
        return RestClient.builder()
                .baseUrl(portOneBaseUrl)  // 포트원 API v1 기본 URL
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // 일반적인 HTTP 요청용 RestClient
    @Bean
    public RestClient generalRestClient() {
        return RestClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // HTTP 인터페이스 클라이언트 프록시 팩토리 (필요한 경우)
    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory(RestClient portOneRestClient) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(portOneRestClient))
                .build();
    }
}
