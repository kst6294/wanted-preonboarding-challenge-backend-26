package wanted.market.item.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemCreateRequestDto {
    @NotBlank
    private String itemName;
    @NotBlank
    private int price;
    @NotBlank
    private int quantity;
}
