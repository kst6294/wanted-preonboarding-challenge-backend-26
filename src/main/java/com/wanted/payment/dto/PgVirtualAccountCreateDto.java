package com.wanted.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PgVirtualAccountCreateDto {
    private int orderId;
    private int price;
}
