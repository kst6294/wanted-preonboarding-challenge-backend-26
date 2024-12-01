package org.wantedpayment.item.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRegisterRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;
}
