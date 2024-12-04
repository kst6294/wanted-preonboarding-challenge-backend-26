package com.wanted.market.domain.payment.service;

import com.wanted.market.common.exception.CustomException;
import com.wanted.market.common.exception.ErrorCode;
import com.wanted.market.domain.payment.Payment;
import com.wanted.market.domain.payment.dto.VirtualAccountInfo;
import com.wanted.market.domain.payment.repository.PaymentRepository;
import com.wanted.market.domain.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MockPortOneApiService mockPortOneApiService;

    @Transactional
    public Payment startPayment(Transaction transaction) {
        // 1. 결제 엔티티 생성
        Payment payment = createPayment(transaction);

        // 2. 가상계좌 발급
        return requestVirtualAccount(payment);
    }

    private Payment createPayment(Transaction transaction) {
        // 중복 결제 체크
        paymentRepository.findByTransaction(transaction).ifPresent(p -> {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        });

        String merchantUid = generateMerchantUid();

        Payment payment = Payment.builder()
                .transaction(transaction)
                .merchantUid(merchantUid)
                .amount(transaction.getPurchasePrice())
                .build();

        return paymentRepository.save(payment);
    }

    private Payment requestVirtualAccount(Payment payment) {
        // Mock PG사 API를 통한 가상계좌 발급 요청
        try {
            VirtualAccountInfo virtualAccount = mockPortOneApiService.requestVirtualAccount(
                    payment.getMerchantUid(),
                    payment.getAmount().longValue()
            );

            // 가상계좌 정보 업데이트
            payment.updateVirtualAccountInfo(
                    virtualAccount.getAccountNumber(),
                    virtualAccount.getBankCode(),
                    virtualAccount.getBankName(),
                    virtualAccount.getAccountHolder(),
                    virtualAccount.getDueDate()
            );

            return paymentRepository.save(payment);
        } catch (Exception e) {
            payment.markAsFailed();
            paymentRepository.save(payment);
            log.error("Failed to request virtual account for payment {}: {}",
                    payment.getMerchantUid(), e.getMessage());
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    @Transactional
    public void confirmPayment(String merchantUid, String impUid) {
        Payment payment = paymentRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // Mock PG사 API를 통한 결제 검증
        boolean isValid = mockPortOneApiService.validatePayment(
                impUid,
                merchantUid,
                payment.getAmount().longValue()
        );

        if (!isValid) {
            payment.markAsFailed();
            paymentRepository.save(payment);
            throw new CustomException(ErrorCode.PAYMENT_VALIDATION_FAILED);
        }

        payment.confirmPayment(impUid);
        paymentRepository.save(payment);
    }

    @Transactional
    public Payment cancelPayment(String merchantUid, String reason) {
        Payment payment = paymentRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!payment.isCancellable()) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        if (payment.getImpUid() != null) {
            // Mock PG사 API를 통한 결제 취소
            mockPortOneApiService.cancelPayment(payment.getImpUid(), reason);
        }

        payment.cancel();
        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment getPaymentByMerchantUid(String merchantUid) {
        return paymentRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private String generateMerchantUid() {
        return "order_" + UUID.randomUUID().toString().replace("-", "");
    }
}
