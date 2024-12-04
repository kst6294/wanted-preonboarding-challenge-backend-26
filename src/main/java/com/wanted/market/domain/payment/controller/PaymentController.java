package com.wanted.market.domain.payment.controller;

import com.wanted.market.common.dto.ResponseDto;
import com.wanted.market.domain.payment.Payment;
import com.wanted.market.domain.payment.dto.PaymentCreateRequest;
import com.wanted.market.domain.payment.dto.PaymentResponse;
import com.wanted.market.domain.payment.service.PaymentService;
import com.wanted.market.domain.transaction.Transaction;
import com.wanted.market.domain.transaction.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerSpec {

    private final PaymentService paymentService;
    private final TransactionService transactionService;

    @Override
    public ResponseEntity<ResponseDto<PaymentResponse>> createPayment(
            @AuthenticationPrincipal String email,
            @Valid PaymentCreateRequest request
    ) {
        // 1. 트랜잭션 조회 및 검증 (이메일을 포함하여 호출)
        Transaction transaction = transactionService.getTransactionForPayment(email, request.getTransactionId());

        // 결제 생성
        Payment payment = paymentService.startPayment(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.success(PaymentResponse.from(payment)));
    }

    @Override
    public ResponseEntity<ResponseDto<PaymentResponse>> getPayment(String merchantUid) {
        Payment payment = paymentService.getPaymentByMerchantUid(merchantUid);
        return ResponseEntity.ok(ResponseDto.success(PaymentResponse.from(payment)));
    }

    @Override
    public ResponseEntity<ResponseDto<PaymentResponse>> cancelPayment(
            @AuthenticationPrincipal String email,
            String merchantUid,
            @NotBlank String reason
    ) {
        // TODO: 결제 취소 권한 검증 로직 추가 필요
        Payment payment = paymentService.cancelPayment(merchantUid, reason);
        return ResponseEntity.ok(ResponseDto.success(PaymentResponse.from(payment)));
    }

    @Override
    public ResponseEntity<Void> handleWebhook(String merchant_uid, String imp_uid) {
        paymentService.confirmPayment(merchant_uid, imp_uid);
        return ResponseEntity.ok().build();
    }
}
