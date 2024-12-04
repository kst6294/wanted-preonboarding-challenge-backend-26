package com.wanted.market.domain.payment.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VirtualAccountInfo {

    private String accountNumber;
    private String bankCode;
    private String bankName;
    private String accountHolder;
    private LocalDateTime dueDate;
}
