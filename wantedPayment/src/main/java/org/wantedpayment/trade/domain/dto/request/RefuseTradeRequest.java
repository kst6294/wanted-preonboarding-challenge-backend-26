package org.wantedpayment.trade.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefuseTradeRequest {
    private Long tradeId;
    private String impUid;
    private BigDecimal amount;
}
