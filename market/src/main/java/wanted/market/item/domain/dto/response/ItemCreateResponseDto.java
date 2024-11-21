package wanted.market.item.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemCreateResponseDto {

    private boolean createSuccess;
    private Long itemId;
    private String itemName;
}
