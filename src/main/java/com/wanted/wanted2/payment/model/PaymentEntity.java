package com.wanted.wanted2.payment.model;

import com.wanted.wanted2.users.model.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
    @Id
    @Column(name = "imp_uid", nullable = false)
    private String impUid;

    @Column(name = "pay_method", nullable = false)
    private PayMethod payMethod;

    @Column(name = "merchant_uid", nullable = false)
    private String merchantUid;

    private String product;

    @Column(nullable = false)
    private BigDecimal amount;

    // 인증결제일 경우에 저장, 비인증결제의 경우 나머지 정보만 저장된다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = true)
    private UserEntity buyer;
}
