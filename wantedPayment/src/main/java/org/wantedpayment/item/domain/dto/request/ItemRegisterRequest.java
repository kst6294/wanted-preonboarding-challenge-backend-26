package org.wantedpayment.item.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRegisterRequest {
    private String name;
    private String description;
    private int price;
    private int quantity;
}
