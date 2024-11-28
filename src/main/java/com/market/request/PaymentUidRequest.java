package com.market.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentUidRequest(
        @NotBlank(message = "결제 아이디는 포함되어야 하고, 빈 문자, 공백일 수 없습니다.")
        String impUid,
        @NotBlank(message = "주문 번호는 포함되어야 하고, 빈 문자, 공백일 수 없습니다.")
        String merchantUid,
        @NotNull(message = "구매자 아이디가 포함되어야 합니다.")
        @Positive(message = "구매자 아이디는 1보다 작을 수 없습니다.")
        Long buyerId) {
}
