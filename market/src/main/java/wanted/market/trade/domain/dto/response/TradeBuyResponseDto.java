package wanted.market.trade.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wanted.market.trade.domain.entity.TradeStatus;

@Getter
@AllArgsConstructor
public class TradeBuyResponseDto {
    private Long tradeId;
    private Long itemId;
    private TradeStatus status;
}
