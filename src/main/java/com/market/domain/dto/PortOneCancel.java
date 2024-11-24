package com.market.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public record PortOneCancel(
        @JsonProperty("imp_uid")
        String impUid,

        @JsonProperty("merchant_uid")
        String merchantUid,

        BigInteger amount,

        BigInteger checksum,

        String reason) {
}
