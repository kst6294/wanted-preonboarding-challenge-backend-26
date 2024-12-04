package wanted.market.item.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wanted.market.item.domain.entity.ItemStatus;

@Getter
@AllArgsConstructor
public class ItemInfoResponseDto {
    private Long itemId;
    private String itemName;
    private int price;
    private int quantity;
    private ItemStatus status;
    private Long sellerId;
    private String sellerName;
}
