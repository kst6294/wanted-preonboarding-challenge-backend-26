package com.wanted.market.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductCreateRequest {
    @NotBlank(message = "상품명은 필수입니다")
    private String name;

    @NotNull(message = "가격은 필수입니다")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다")
    private BigDecimal price;

    @NotNull(message = "수량은 필수입니다")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다")
    private Integer quantity;
}