package com.wanted.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VirtualAccountInfoDto {
    private String paymentId;
    private String bankName; // 은행명
    private String bankNum; // 계좌번호
    private Integer bankDate; // 가상 계좌 입금 기한
}
