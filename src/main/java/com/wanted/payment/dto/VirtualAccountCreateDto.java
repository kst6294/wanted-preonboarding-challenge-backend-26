package com.wanted.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VirtualAccountCreateDto {
    private int orderId;
    private int price;
}
