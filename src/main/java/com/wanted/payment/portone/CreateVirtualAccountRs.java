package com.wanted.payment.portone;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateVirtualAccountRs {
    private String imp_uid;
    private String merchant_uid;
    private String vbank_name; // 은행명
    private String vbank_num; // 계좌번호
    private Integer vbank_date; // 가상 계좌 입금 기한
}
