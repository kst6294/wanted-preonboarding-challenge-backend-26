package com.market.domain.dto;

import java.math.BigInteger;

public record OrderRequest(
        Long productId,
        Long sellerId,
        Long buyerId,
        String merchantUid,
        BigInteger amount) {
}
