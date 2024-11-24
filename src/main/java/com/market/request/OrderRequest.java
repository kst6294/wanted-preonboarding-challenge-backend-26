package com.market.request;

import java.math.BigInteger;

public record OrderRequest(
        Long productId,
        Long sellerId,
        Long buyerId,
        String merchantUid,
        BigInteger amount) {
}
