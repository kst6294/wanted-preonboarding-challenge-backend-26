package com.market.response;

import com.market.domain.entity.ReservationStatus;
import com.querydsl.core.annotations.QueryProjection;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record SimpleOrderResponse(Long id, String merchantUid, LocalDateTime orderedAt,
                                  String sellerName, String buyerName,
                                  String productName, int quantity, BigInteger totalAmount, String reservationStatus) {
    @QueryProjection
    public SimpleOrderResponse(Long id, String merchantUid, LocalDateTime orderedAt,
                               String sellerName, String buyerName,
                               String productName, int quantity, BigInteger totalAmount,
                               ReservationStatus reservationStatus) {
        this(id, merchantUid, orderedAt, sellerName, buyerName,
                productName, quantity, totalAmount, reservationStatus.getName());
    }
}
