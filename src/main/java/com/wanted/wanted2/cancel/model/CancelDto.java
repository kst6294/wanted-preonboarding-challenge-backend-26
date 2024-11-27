package com.wanted.wanted2.cancel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelDto {
    private String id;
    private String reason;
    private Long orderId;
    private BigDecimal cancelAmount;
    private String refundHolder;
    private String refundBank;
    private String refundAccount;
}
