// PaymentStatus.java
package com.wanted.market.domain.payment;

import com.wanted.market.common.exception.CustomException;
import com.wanted.market.common.exception.ErrorCode;
import com.wanted.market.domain.base.BaseEntity;
import com.wanted.market.domain.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_transaction", columnList = "transaction_id"),
                @Index(name = "idx_merchant_uid", columnList = "merchant_uid"),
                @Index(name = "idx_status", columnList = "status")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @NotNull
    @Column(nullable = false, unique = true)
    private String merchantUid;  // 주문번호

    @Column(unique = true)
    private String impUid;       // 포트원 결제 고유번호

    @NotNull
    @Column(nullable = false)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // 가상계좌 정보
    private String virtualAccount;        // 가상계좌 번호
    private String virtualBankCode;       // 은행 코드
    private String virtualBankName;       // 은행명
    private String virtualAccountHolder;  // 예금주
    private LocalDateTime virtualDueDate; // 입금기한

    @Version
    private Long version;

    @Builder
    private Payment(Transaction transaction, String merchantUid, BigDecimal amount) {
        validatePaymentInfo(transaction, merchantUid, amount);

        this.transaction = transaction;
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.method = PaymentMethod.VIRTUAL_ACCOUNT;
        this.status = PaymentStatus.READY;
    }

    public void updateVirtualAccountInfo(
            String accountNumber,
            String bankCode,
            String bankName,
            String accountHolder,
            LocalDateTime dueDate
    ) {
        if (this.status != PaymentStatus.READY) {
            throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
        }

        this.virtualAccount = accountNumber;
        this.virtualBankCode = bankCode;
        this.virtualBankName = bankName;
        this.virtualAccountHolder = accountHolder;
        this.virtualDueDate = dueDate;
        this.status = PaymentStatus.PENDING;
    }

    public void confirmPayment(String impUid) {
        if (this.status != PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
        }

        this.impUid = impUid;
        this.status = PaymentStatus.PAID;
    }

    public void markAsFailed() {
        if (this.status != PaymentStatus.PENDING && this.status != PaymentStatus.READY) {
            throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
        }
        this.status = PaymentStatus.FAILED;
    }

    public void cancel() {
        if (this.status != PaymentStatus.PAID) {
            throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
        }
        this.status = PaymentStatus.CANCELLED;
    }

    private void validatePaymentInfo(Transaction transaction, String merchantUid, BigDecimal amount) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction must not be null");
        }
        if (merchantUid == null || merchantUid.isBlank()) {
            throw new IllegalArgumentException("Merchant UID must not be empty");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    public boolean isCancellable() {
        return this.status == PaymentStatus.PAID;
    }
}
