package org.wantedpayment.item.domain.entity;

import lombok.Getter;

@Getter
public enum ItemStatus {
    ON_SALE("판매중"),
    RESERVED("예약중"),
    COMPLETED("완료");


    private final String status;

    ItemStatus(String status) {
        this.status = status;
    }
}
