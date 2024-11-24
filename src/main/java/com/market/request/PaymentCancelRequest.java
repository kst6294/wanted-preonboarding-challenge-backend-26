package com.market.request;

import java.math.BigInteger;

public record PaymentCancelRequest(String merchantUid, BigInteger cancelAmount, String reason, Long buyerId) {
}
