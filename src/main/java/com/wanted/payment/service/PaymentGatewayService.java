package com.wanted.payment.service;

import com.wanted.payment.dto.PaymentCheckDto;

public interface PaymentGatewayService {
    boolean paymentCheck(PaymentCheckDto dto);
    void paymentCancel();
}
