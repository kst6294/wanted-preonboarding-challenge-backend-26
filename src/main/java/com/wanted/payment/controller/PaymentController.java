package com.wanted.payment.controller;

import com.wanted.payment.dto.PaymentCancelDto;
import com.wanted.payment.dto.PaymentCompleteDto;
import com.wanted.payment.rqrs.CreateVirtualBankRs;
import com.wanted.payment.rqrs.PaymentCancelRq;
import com.wanted.payment.rqrs.PaymentRq;
import com.wanted.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController("/order")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{orderId}/payment/{paymentId}")
    public void payment(@PathVariable int orderId, @PathVariable String paymentId) {
        paymentService.payment(new PaymentCompleteDto(paymentId, orderId));
    }

    @PostMapping("/{orderId}/payment/virtual")
    public CreateVirtualBankRs virtualPayment(@PathVariable int orderId) {
        return paymentService.createVirtualBank(orderId);
    }

    @DeleteMapping("/{orderId}/payment/{paymentId}")
    public void cancel(@PathVariable int orderId, @PathVariable String paymentId, @RequestBody PaymentCancelRq rq) {
        paymentService.paymentCancel(PaymentCancelDto.of(paymentId, orderId, rq));
    }
}
