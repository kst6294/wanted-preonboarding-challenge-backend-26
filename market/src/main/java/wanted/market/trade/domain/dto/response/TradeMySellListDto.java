package wanted.market.trade.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wanted.market.trade.domain.entity.TradeStatus;

@Getter
@AllArgsConstructor
public class TradeMySellListDto {
    private Long tradeId;
    private Long itemId;
    private int price;
    private Long buyerId;
    private String buyerName;
    private TradeStatus tradeStatus;
}
