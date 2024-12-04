package com.wanted.market.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCreateRequest {
    @NotNull
    @Positive
    private Long transactionId;
}
