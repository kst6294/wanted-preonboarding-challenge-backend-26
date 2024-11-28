package org.wantedpayment.trade.domain.entity;

import lombok.Getter;

@Getter
public enum TradeStatus {
    ACCEPTED("판매승인"),
    REQUESTED("구매요청"),
    CONFIRMED("구매확정");

    private final String status;

    TradeStatus(String status) {
        this.status = status;
    }
}
