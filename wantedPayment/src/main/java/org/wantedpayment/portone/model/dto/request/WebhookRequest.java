package org.wantedpayment.portone.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebhookRequest {
    private String impUid;
    private String merchantUid;
    private String status;
    private String cancellationId;
}
