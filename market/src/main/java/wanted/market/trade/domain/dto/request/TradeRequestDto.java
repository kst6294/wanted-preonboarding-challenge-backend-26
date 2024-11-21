package wanted.market.trade.domain.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class TradeRequestDto {
    @NotBlank
    private Long itemId;
}
