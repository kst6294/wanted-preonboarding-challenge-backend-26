package wanted.market.item.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wanted.market.item.domain.entity.ItemStatus;

@Getter
@AllArgsConstructor
public class ItemListSearchResponseDto {
    private Long itemId;
    private String itemName;
    private int price;
    private int quantity;
    private ItemStatus itemStatus;
}
