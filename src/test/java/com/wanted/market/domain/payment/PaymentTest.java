package com.wanted.market.domain.payment;

import com.wanted.market.common.exception.CustomException;
import com.wanted.market.common.exception.ErrorCode;
import com.wanted.market.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.wanted.market.common.fixture.TestFixture.createPayment;
import static com.wanted.market.common.fixture.TestFixture.createTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Payment 도메인 테스트")
class PaymentTest {
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
    }

    @Test
    @DisplayName("결제 생성 시 기본 상태는 READY이다")
    void canCreatePayment() {
        Payment payment = createPayment(transaction);
        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(payment.getMethod()).isEqualTo(PaymentMethod.VIRTUAL_ACCOUNT);
    }

    @Test
    @DisplayName("가상계좌 발급 시 상태가 PENDING으로 변경된다")
    void updateVirtualAccountInfo() {
        // given
        Payment payment = createPayment(transaction);
        String accountNumber = "1234567890";
        String bankCode = "088";
        String bankName = "신한은행";
        String accountHolder = "홍길동";
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);

        // when
        payment.updateVirtualAccountInfo(
                accountNumber, bankCode, bankName, accountHolder, dueDate
        );

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getVirtualAccount()).isEqualTo(accountNumber);
        assertThat(payment.getVirtualBankCode()).isEqualTo(bankCode);
    }

    @Test
    @DisplayName("READY 상태가 아닐 때 가상계좌 발급을 시도하면 예외가 발생한다")
    void shouldThrowExceptionWhenUpdateVirtualAccountInfoInInvalidStatus() {
        // given
        Payment payment = createPayment(transaction);
        payment.markAsFailed(); // FAILED 상태로 변경

        // when & then
        assertThatThrownBy(() ->
                payment.updateVirtualAccountInfo(
                        "1234567890", "088", "신한은행", "홍길동",
                        LocalDateTime.now().plusDays(7)
                )
        ).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_STATUS_UPDATE.getMessage());
    }

    @Test
    @DisplayName("결제 승인이 정상적으로 처리된다")
    void confirmPayment() {
        // given
        Payment payment = createPayment(transaction);
        payment.updateVirtualAccountInfo("1234567890", "088", "신한은행", "홍길동",
                LocalDateTime.now().plusDays(7));

        // when
        payment.confirmPayment("imp_uid");

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(payment.getImpUid()).isEqualTo("imp_uid");
    }

    @Test
    @DisplayName("결제 취소가 정상적으로 처리된다")
    void cancelPayment() {
        // given
        Payment payment = createPayment(transaction);
        payment.updateVirtualAccountInfo("1234567890", "088", "신한은행", "홍길동",
                LocalDateTime.now().plusDays(7));

        payment.confirmPayment("imp_uid");
        // when
        payment.cancel();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }
}
