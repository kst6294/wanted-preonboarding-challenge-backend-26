package com.wanted.payment.service;

import com.wanted.payment.dto.PgPaymentCancelDto;
import com.wanted.payment.dto.PgPaymentCheckDto;
import com.wanted.payment.dto.PgVirtualAccountCreateDto;
import com.wanted.payment.dto.VirtualAccountInfoDto;

public interface PaymentGatewayService {
    boolean paymentCheck(PgPaymentCheckDto dto);
    VirtualAccountInfoDto createVirtualAccount(PgVirtualAccountCreateDto dto);
    void paymentCancel(PgPaymentCancelDto dto);
}
