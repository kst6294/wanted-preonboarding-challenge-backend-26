package com.wanted.payment.controller;

import com.wanted.payment.dto.PaymentCompleteDto;
import com.wanted.payment.rqrs.CreateVirtualBankRs;
import com.wanted.payment.rqrs.PaymentRq;
import com.wanted.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("{orderId}")
    public void payment(@PathVariable int orderId, @RequestBody PaymentRq request) {
        paymentService.payment(new PaymentCompleteDto(request.getPaymentId(), orderId));
    }

    @PostMapping("/virtual/{orderId}")
    public CreateVirtualBankRs virtualPayment(@PathVariable int orderId) {
        return paymentService.createVirtualBank(orderId);
    }

    @DeleteMapping
    public void cancel() {
        // request order id (주문 번호)
        // 결제 정보 조회 및 대행 서비스에 요청 보내기
    }
}
