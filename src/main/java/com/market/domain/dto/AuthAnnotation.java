package com.market.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthAnnotation(@JsonProperty("access_token") String accessToken,
                             @JsonProperty("expired_at") int expiredAt,
                             @JsonProperty("now") int now) {
}
