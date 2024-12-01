package com.wanted.payment.portone;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateVirtualAccountRq {
    private String merchant_uid; // 고객사 주문번호
    private int amount;
    private String vbank_code;
    private Integer vbank_due;
}
