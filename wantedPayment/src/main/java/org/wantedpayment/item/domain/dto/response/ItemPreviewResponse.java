package org.wantedpayment.item.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.wantedpayment.item.domain.entity.ItemStatus;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ItemPreviewResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private int quantity;
    private ItemStatus status;
}
