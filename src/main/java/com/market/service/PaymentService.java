package com.market.service;

import com.market.domain.dto.PaymentAnnotation;
import com.market.domain.dto.PortOneAccessToken;
import com.market.domain.dto.PortOneAuth;
import com.market.domain.dto.PortOneCancel;
import com.market.domain.entity.Payments;
import com.market.repository.PaymentRepository;
import com.market.request.PaymentCancelRequest;
import com.market.request.PaymentUidRequest;
import com.market.response.SimpleOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String BASE_URL = "https://api.iamport.kr";

    private final RestClient restClient;
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;

    @Value("${imp.api.v1.key}")
    private String impKey;

    @Value("${imp.api.v1.secret}")
    private String impSecret;

    @Transactional
    public Payments complete(PaymentUidRequest paymentUidRequest) {
        ResponseEntity<PortOneAccessToken> tokenResponse = restClient.post()
                .uri(BASE_URL + "/users/getToken")
                .contentType(APPLICATION_JSON)
                .body(new PortOneAuth(impKey, impSecret))
                .retrieve()
                .toEntity(PortOneAccessToken.class);
        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            log.info("tokenResponse= {}", tokenResponse);
            throw new IllegalStateException(tokenResponse.toString());
        }

        PortOneAccessToken portOneAccessToken = tokenResponse.getBody();
        if (portOneAccessToken == null) {
            throw new IllegalStateException("유효하지 않는 토큰입니다");
        }

        ResponseEntity<PaymentAnnotation> paymentResponse = restClient.get()
                .uri(BASE_URL + "/payments/{impUid}", paymentUidRequest.impUid())
                .header("Authorization", portOneAccessToken.accessToken())
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(PaymentAnnotation.class);
        if (paymentResponse.getStatusCode() != HttpStatus.OK) {
            log.info("paymentResponse= {}", paymentResponse);
            throw new IllegalStateException(paymentResponse.toString());
        }

        PaymentAnnotation payment = paymentResponse.getBody();
        if (payment == null) {
            throw new IllegalStateException("유효하지 않는 결제입니다.");
        }

        SimpleOrderResponse order = orderService.findByMerchantUid(paymentUidRequest.merchantUid());
        if (!Objects.equals(order.totalAmount(), payment.amount())) {
            throw new IllegalStateException("결제 검증에 실패했습니다.");
        }
        if (!payment.status().equals("paid")) {
            throw new RuntimeException("결제 미완료");
        }

        // TODO : 제품 재고도 차감해야 하지만 제품 기능은 구현하지 않으므로 제외
        // TODO : 고객 기능은 구현하지 않으므로 요청에 구매자 id를 받아서 처리하도록 구현
        orderService.updateReservationDone(order.id());

        LocalDateTime paidAt = Instant.ofEpochSecond(payment.paidAt())
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
        return paymentRepository.save(Payments.builder()
                .buyerId(paymentUidRequest.buyerId())
                .impUid(paymentUidRequest.impUid())
                .merchantUid(paymentUidRequest.merchantUid())
                .amount(payment.amount())
                .cancelAmount(payment.cancelAmount())
                .payMethod(payment.payMethod())
                .status(payment.status())
                .paidAt(paidAt)
                .build());
    }

    @Transactional
    public Payments cancel(PaymentCancelRequest paymentCancelRequest) {
        ResponseEntity<PortOneAccessToken> tokenResponse = restClient.post()
                .uri(BASE_URL + "/users/getToken")
                .contentType(APPLICATION_JSON)
                .body(new PortOneAuth(impKey, impSecret))
                .retrieve()
                .toEntity(PortOneAccessToken.class);
        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            log.info("tokenResponse= {}", tokenResponse);
            throw new IllegalStateException(tokenResponse.toString());
        }

        PortOneAccessToken portOneAccessToken = tokenResponse.getBody();
        if (portOneAccessToken == null) {
            throw new IllegalStateException("유효하지 않는 토큰입니다");
        }

        Payments findPayment = paymentRepository.findByMerchantUid(paymentCancelRequest.merchantUid());

        BigInteger cancelableAmount = findPayment.getAmount().subtract(findPayment.getCancelAmount());
        if (cancelableAmount.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalStateException("이미 전액환불된 주문입니다.");
        }

        PortOneCancel cancelData = new PortOneCancel(findPayment.getImpUid(), findPayment.getMerchantUid(),
                findPayment.getAmount(), cancelableAmount, paymentCancelRequest.reason());

        ResponseEntity<PaymentAnnotation> paymentResponse = restClient.post()
                .uri(BASE_URL + "/payments/cancel")
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(APPLICATION_JSON);
                    httpHeaders.set("Authorization", portOneAccessToken.accessToken());
                })
                .body(cancelData)
                .retrieve()
                .toEntity(PaymentAnnotation.class);

        PaymentAnnotation paymentAnnotation = paymentResponse.getBody();
        if (paymentAnnotation == null) {
            throw new IllegalStateException("유효하지 않는 결제입니다.");
        }

        Payments payment = paymentRepository.findByImpUid(paymentAnnotation.impUid());
        payment.setCancelAmount(paymentAnnotation.cancelAmount());
        payment.setStatus(paymentAnnotation.status());
        payment.setPaidAt(
                Instant.ofEpochSecond(paymentAnnotation.paidAt())
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toLocalDateTime());

        return payment;
    }
}
