package com.market.request;

public record PaymentUidRequest(String impUid, String merchantUid, Long buyerId) {
}
