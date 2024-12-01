package org.wantedpayment.trade.domain.entity;

import lombok.Getter;

@Getter
public enum TradeStatus {
    REQUESTED("구매요청"),
    ACCEPTED("판매승인"),
    CONFIRMED("구매확정"),
    CANCELED("결제취소");

    private final String status;

    TradeStatus(String status) {
        this.status = status;
    }
}
