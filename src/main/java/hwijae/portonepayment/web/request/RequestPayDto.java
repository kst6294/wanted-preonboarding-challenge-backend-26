package hwijae.portonepayment.web.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestPayDto {
    private String orderUid;
    private String itemName;
    private String buyerName;
    private Long paymentPrice;
    private String buyerEmail;

    @Builder
    public RequestPayDto(String orderUid, String itemName, String buyerName, Long paymentPrice, String buyerEmail ) {
        this.orderUid = orderUid;
        this.itemName = itemName;
        this.buyerName = buyerName;
        this.paymentPrice = paymentPrice;
        this.buyerEmail = buyerEmail;
    }
}
