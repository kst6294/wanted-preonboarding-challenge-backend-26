package com.market.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigInteger;

public record OrderRequest(
        @NotNull(message = "제품 아이디가 포함되어야 합니다.")
        @Positive(message = "제품 아이디는 1보다 작을 수 없습니다.")
        Long productId,
        @NotNull(message = "판매자 아이디가 포함되어야 합니다.")
        @Positive(message = "판매자 아이디는 1보다 작을 수 없습니다.")
        Long sellerId,
        @NotNull(message = "구매자 아이디가 포함되어야 합니다.")
        @Positive(message = "구매자 아이디는 1보다 작을 수 없습니다.")
        Long buyerId,
        @NotBlank(message = "주문 번호는 포함되어야 하고, 빈 문자, 공백일 수 없습니다.")
        String merchantUid,
        @NotNull(message = "주문 금액은 포함되어야 합니다.")
        @Positive(message = "주문 금액은 1보다 작을 수 없습니다.")
        BigInteger amount) {
}
