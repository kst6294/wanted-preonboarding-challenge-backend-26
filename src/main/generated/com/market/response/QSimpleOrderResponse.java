package com.market.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.market.response.QSimpleOrderResponse is a Querydsl Projection type for SimpleOrderResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QSimpleOrderResponse extends ConstructorExpression<SimpleOrderResponse> {

    private static final long serialVersionUID = -1501543851L;

    public QSimpleOrderResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> merchantUid, com.querydsl.core.types.Expression<java.time.LocalDateTime> orderedAt, com.querydsl.core.types.Expression<String> sellerName, com.querydsl.core.types.Expression<String> buyerName, com.querydsl.core.types.Expression<String> productName, com.querydsl.core.types.Expression<Integer> quantity, com.querydsl.core.types.Expression<? extends java.math.BigInteger> totalAmount, com.querydsl.core.types.Expression<com.market.domain.entity.ReservationStatus> reservationStatus) {
        super(SimpleOrderResponse.class, new Class<?>[]{long.class, String.class, java.time.LocalDateTime.class, String.class, String.class, String.class, int.class, java.math.BigInteger.class, com.market.domain.entity.ReservationStatus.class}, id, merchantUid, orderedAt, sellerName, buyerName, productName, quantity, totalAmount, reservationStatus);
    }

}

