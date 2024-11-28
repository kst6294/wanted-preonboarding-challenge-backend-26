package org.wantedpayment.global.portone.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PortOneAccessTokenRequest {
    @Value("${portone.api.key}")
    private String imp_key;
    @Value("${portone.api.secret}")
    private String imp_secret;
}
