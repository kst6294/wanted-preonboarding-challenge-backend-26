package com.wanted.payment.service;

import com.wanted.payment.dto.PaymentCheckDto;
import com.wanted.payment.dto.VirtualAccountCreateDto;
import com.wanted.payment.dto.VirtualAccountInfoDto;

public interface PaymentGatewayService {
    boolean paymentCheck(PaymentCheckDto dto);
    VirtualAccountInfoDto createVirtualAccount(VirtualAccountCreateDto dto);
    void paymentCancel();
}
