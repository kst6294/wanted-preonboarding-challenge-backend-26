package com.wanted.wanted2.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private String impUid;
    private String merchantUid;
    private String payMethod;
    private BigDecimal amount;
    private Long productId;
    private Long buyerId;
}
