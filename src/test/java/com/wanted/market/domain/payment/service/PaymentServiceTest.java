package com.wanted.market.domain.payment.service;

import com.wanted.market.common.exception.CustomException;
import com.wanted.market.common.fixture.TestFixture;
import com.wanted.market.domain.payment.Payment;
import com.wanted.market.domain.payment.PaymentStatus;
import com.wanted.market.domain.payment.dto.VirtualAccountInfo;
import com.wanted.market.domain.payment.repository.PaymentRepository;
import com.wanted.market.domain.transaction.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment 서비스 테스트")
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MockPortOneApiService mockPortOneApiService;

    @Test
    @DisplayName("결제 시작 시 가상계좌가 정상적으로 발급된다")
    void startPayment() {
        // given
        Transaction transaction = TestFixture.createTransaction();
        // 결제를 시작하면, READY 상태로 생성
        Payment readyPayment = TestFixture.payment(transaction).build();

        VirtualAccountInfo virtualAccountInfo = VirtualAccountInfo.builder()
                .accountNumber("0123456789")
                .bankCode("088")
                .bankName("신한은행")
                .accountHolder("원티드마켓")
                .dueDate(LocalDateTime.now().plusDays(7))
                .build();

        given(paymentRepository.findByTransaction(transaction))
                .willReturn(Optional.empty());
        given(paymentRepository.save(any(Payment.class)))
                .willReturn(readyPayment);
        given(mockPortOneApiService.requestVirtualAccount(
                readyPayment.getMerchantUid(),
                readyPayment.getAmount().longValue())
        ).willReturn(virtualAccountInfo);

        // when
        Payment result = paymentService.startPayment(transaction);

        // then
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.getVirtualAccount()).isEqualTo(virtualAccountInfo.getAccountNumber());
        assertThat(result.getVirtualBankCode()).isEqualTo(virtualAccountInfo.getBankCode());
        assertThat(result.getVirtualBankName()).isEqualTo(virtualAccountInfo.getBankName());
        assertThat(result.getVirtualAccountHolder()).isEqualTo(virtualAccountInfo.getAccountHolder());
    }

    @Test
    @DisplayName("이미 결제가 존재하는 거래에 대해 결제를 시작할 수 없다")
    void cannotStartPaymentForExistingPayment() {
        // given
        Transaction transaction = TestFixture.createTransaction();
        Payment existingPayment = TestFixture.createPayment(transaction);

        given(paymentRepository.findByTransaction(transaction))
                .willReturn(Optional.of(existingPayment));

        // when & then
        assertThatThrownBy(() -> paymentService.startPayment(transaction))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("결제 확인 시 검증에 성공하면 결제가 완료된다")
    void confirmPayment() {
        // given
        Transaction transaction = TestFixture.createTransaction();
        // PENDING 상태의 Payment (가상계좌가 발급된 상태)
        Payment payment = TestFixture.payment(transaction)
                .withVirtualAccount()  // PENDING 상태로 설정하고 가상계좌 정보도 설정
                .build();

        String merchantUid = payment.getMerchantUid();
        String impUid = "imp_" + System.currentTimeMillis();

        given(paymentRepository.findByMerchantUid(merchantUid))
                .willReturn(Optional.of(payment));
        given(mockPortOneApiService.validatePayment(
                impUid,
                merchantUid,
                payment.getAmount().longValue())
        ).willReturn(true);

        // when
        paymentService.confirmPayment(merchantUid, impUid);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(payment.getImpUid()).isEqualTo(impUid);
        verify(paymentRepository).save(payment);
    }


    @Test
    @DisplayName("결제 검증 실패 시 결제가 실패 상태가 된다")
    void paymentFailsWhenValidationFails() {
        // given
        Transaction transaction = TestFixture.createTransaction();
        Payment payment = TestFixture.payment(transaction)
                .withVirtualAccount() // PENDING 상태로 시작
                .build();

        String merchantUid = payment.getMerchantUid();
        String impUid = "imp_123456789";

        given(paymentRepository.findByMerchantUid(merchantUid))
                .willReturn(Optional.of(payment));
        given(mockPortOneApiService.validatePayment(any(), any(), any()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() ->
                paymentService.confirmPayment(merchantUid, impUid)
        )
                .isInstanceOf(CustomException.class);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("결제 취소가 정상적으로 처리된다")
    void cancelPayment() {
        // given
        Transaction transaction = TestFixture.createTransaction();
        Payment payment = TestFixture.payment(transaction)
                .withConfirmedPayment()  // 가상계좌 발급 및 결제 완료 상태로 설정
                .build();

        String merchantUid = payment.getMerchantUid();
        String reason = "테스트 취소";

        given(paymentRepository.findByMerchantUid(merchantUid))
                .willReturn(Optional.of(payment));
        // 저장 후 리턴되는 payment mock 추가
        given(paymentRepository.save(payment)).willReturn(payment);

        // when
        Payment cancelledPayment = paymentService.cancelPayment(merchantUid, reason);

        // then
        assertThat(cancelledPayment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        verify(mockPortOneApiService).cancelPayment(payment.getImpUid(), reason);
        verify(paymentRepository).save(payment);
    }
}
