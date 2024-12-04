package com.wanted.market.domain.payment.dto;

import com.wanted.market.domain.payment.Payment;
import com.wanted.market.domain.payment.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    @Schema(description = "Payment ID", example = "1234")
    private Long id;
    @NotNull
    @Size(min = 1, max = 64)
    @Schema(description = "Merchant's unique order identifier", example = "ORDER_123456")
    private String merchantUid;

    @Size(max = 64)
    @Schema(description = "Payment gateway's unique transaction identifier", example = "IMP_123456")
    private String impUid;
    @NotNull
    @Schema(description = "Payment amount", example = "50000.00")
    private BigDecimal amount;
    private PaymentStatus status;
    @Size (max = 20)
    @Schema (description = "Virtual account number", example = "1234567890")
    private String virtualAccount;
    @Size (max = 50)
    @Schema(description ="Virtual account bank name", example ="신한은행")
    private String virtualBankName;
    @Size (max = 100)
    @Schema (description = "Virtual account holder name", example = "g₴5")
    private String virtualAccountHolder;
    @Schema (description = "Virtual account due date", example = "2024-12-31T23:59:59")
    private LocalDateTime virtualDueDate;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .merchantUid(payment.getMerchantUid())
                .impUid(payment.getImpUid())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .virtualAccount(payment.getVirtualAccount())
                .virtualBankName(payment.getVirtualBankName())
                .virtualAccountHolder(payment.getVirtualAccountHolder())
                .virtualDueDate(payment.getVirtualDueDate())
                .build();
    }
}
