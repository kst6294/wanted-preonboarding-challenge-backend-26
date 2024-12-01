package com.wanted.payment.controller;

import com.wanted.payment.dto.PaymentCompleteDto;
import com.wanted.payment.rqrs.PaymentRequest;
import com.wanted.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping()
    public void payment(@RequestBody PaymentRequest request) {
        paymentService.payment(new PaymentCompleteDto(request.getPaymentId(), request.getOrderId()));
    }

    @PostMapping("/virtual")
    public void virtualPayment() {
    }

    @DeleteMapping
    public void cancel() {
        // request order id (주문 번호)
        // 결제 정보 조회 및 대행 서비스에 요청 보내기
    }
}
