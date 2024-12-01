package com.wanted.payment.rqrs;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateVirtualBankRs {
    private String paymentId;
    private String bankName; // 은행명
    private String bankNum; // 계좌번호
    private Integer bankDate; // 가상 계좌 입금 기한
}
