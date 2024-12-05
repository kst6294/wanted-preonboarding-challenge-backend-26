package org.wantedpayment.item.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.wantedpayment.item.domain.entity.ItemStatus;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ItemDetailResponse {
    private Long id;
    private String sellerName;
    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;
    private ItemStatus status;
}
