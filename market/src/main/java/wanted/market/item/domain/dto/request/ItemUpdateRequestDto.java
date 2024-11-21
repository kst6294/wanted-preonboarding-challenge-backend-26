package wanted.market.item.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemUpdateRequestDto {
    @NotBlank
    private Long itemId;
    @NotBlank
    private String itemName;
    @NotBlank
    private int price;
    @NotBlank
    private int quantity;
}
