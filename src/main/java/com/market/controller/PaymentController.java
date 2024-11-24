package com.market.controller;

import com.market.domain.entity.Payments;
import com.market.request.PaymentCancelRequest;
import com.market.request.PaymentUidRequest;
import com.market.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/complete")
    public ResponseEntity<Void> paymentComplete(@RequestBody PaymentUidRequest paymentUidRequest) {
        log.info("paymentUidRequest= {}", paymentUidRequest);
        Payments payment = paymentService.complete(paymentUidRequest);
        if (payment == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/cancel")
    public void paymentCancel(@RequestBody PaymentCancelRequest paymentCancelRequest) {
        log.info("paymentCancelRequest= {}", paymentCancelRequest);
        paymentService.cancel(paymentCancelRequest);
    }
}
