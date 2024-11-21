package wanted.market.item.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemUpdateResponseDto {
    private boolean updateSuccess;
    private Long itemId;
    private String itemName;
    private int price;
    private int quantity;
}
