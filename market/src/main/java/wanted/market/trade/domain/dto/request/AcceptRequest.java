package wanted.market.trade.domain.dto.request;

import lombok.Getter;
import wanted.market.trade.domain.entity.AcceptStatus;

@Getter
public class AcceptRequest {
    private AcceptStatus acceptStatus;
}
