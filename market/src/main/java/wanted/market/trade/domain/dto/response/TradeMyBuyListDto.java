package wanted.market.trade.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wanted.market.trade.domain.entity.TradeStatus;

@Getter
@AllArgsConstructor
public class TradeMyBuyListDto {
    private Long tradeId;
    private Long itemId;
    private int price;
    private Long sellerId;
    private String sellerName;
    private TradeStatus tradeStatus;
}
