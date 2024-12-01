package org.wantedpayment.trade.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CheckPurchaseRequest {
    private Long tradeId;
    private String impUid;
}
