package org.wantedpayment.portone.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebhookResponse {
    private String impUid;
    private String merchantUid;
    private String status;
    private String cancellationId;
}
