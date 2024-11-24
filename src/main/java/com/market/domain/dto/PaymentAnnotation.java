package com.market.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public record PaymentAnnotation(
        @JsonProperty("imp_uid")
        String impUid,

        @JsonProperty("merchant_uid")
        String merchantUid,

        @JsonProperty("pay_method")
        String payMethod,

        BigInteger amount,

        @JsonProperty("cancel_amount")
        BigInteger cancelAmount,

        String status,

        @JsonProperty("paid_at")
        long paidAt) {
}
