package com.market.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    SALE("판매중"),
    RESERVATION("예약중"),
    DONE("완료");

    private final String name;
}
