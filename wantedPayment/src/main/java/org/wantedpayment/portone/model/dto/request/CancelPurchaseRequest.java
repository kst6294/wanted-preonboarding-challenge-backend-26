package org.wantedpayment.portone.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CancelPurchaseRequest {
    private Long tradeId;
    private String impUid;
    private BigDecimal amount;
    private String reason;
    private String refundHolder;
    private String refundAccount;
    private String refundBank;
}
