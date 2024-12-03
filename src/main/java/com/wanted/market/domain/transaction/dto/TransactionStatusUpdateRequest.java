package com.wanted.market.domain.transaction.dto;

import com.wanted.market.domain.transaction.TransactionStatus;

public record TransactionStatusUpdateRequest(
        TransactionStatus status
) {
}
