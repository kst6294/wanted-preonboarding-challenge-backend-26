package org.wantedpayment.global.portone;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.wantedpayment.global.portone.model.PortOneAccessTokenRequest;
import org.wantedpayment.member.repository.MemberRepository;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class PortOneService {
    private final URI accessTokenUri = URI.create("https://api.iamport.kr/users/getToken");

    private final MemberRepository memberRepository;
    private final WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public void createAccessToken() {
        PortOneAccessTokenRequest request = new PortOneAccessTokenRequest();

        log.info("Create PortOne Access Token");

        ResponseEntity<String> response = webClient.post()
                .uri(accessTokenUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve() // client message 전송
                .toEntity(String.class)
                .block();

        if (response == null || response.getStatusCode().isError()) {
            throw new RuntimeException("FAILED TO SEND EMAIL");
        }

        log.info(response.getBody());
    }
}
