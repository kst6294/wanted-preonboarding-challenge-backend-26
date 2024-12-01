package org.wantedpayment.portone.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PreparationRequest {
    private String merchantUid;
    private int amount;
}
