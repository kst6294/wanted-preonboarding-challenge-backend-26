package org.wantedpayment.item.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.wantedpayment.item.domain.entity.ItemStatus;

@Getter
@AllArgsConstructor
public class ItemPreviewResponse {
    private Long id;
    private String name;
    private int price;
    private ItemStatus status;
}
