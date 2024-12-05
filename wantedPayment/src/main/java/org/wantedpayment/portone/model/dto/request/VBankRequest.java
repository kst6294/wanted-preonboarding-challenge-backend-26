package org.wantedpayment.portone.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VBankRequest {
    private String merchantUid;
    private BigDecimal amount;
    private String vbankCode;
    private int vbankDue;
}
