package wanted.market.portone.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WebHook {
    private String imp_uid; // : 결제번호
    private String merchant_uid; //: 주문번호
    private String status; //: 결제 결과
    private String cancellation_id;
}
