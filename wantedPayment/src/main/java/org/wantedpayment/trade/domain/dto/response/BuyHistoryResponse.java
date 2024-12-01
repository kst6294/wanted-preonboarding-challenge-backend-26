package org.wantedpayment.trade.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.wantedpayment.trade.domain.entity.TradeStatus;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BuyHistoryResponse {
    private Long tradeId;
    private BigDecimal tradePrice;
    private TradeStatus tradeStatus;
    private Long itemId;
    private String itemName;
    private Long sellerId;
    private String sellerName;
}
