package com.market.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PortOneAuth(@JsonProperty("imp_key") String impKey, @JsonProperty("imp_secret") String impSecret) {
}
