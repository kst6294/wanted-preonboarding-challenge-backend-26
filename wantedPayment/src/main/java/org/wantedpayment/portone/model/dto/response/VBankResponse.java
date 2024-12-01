package org.wantedpayment.portone.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VBankResponse {
    private String impUid;
    private String merchantUid;
    private BigDecimal amount;
    private BigDecimal cancelAmount;
    private String currency;
    private String status;
}
