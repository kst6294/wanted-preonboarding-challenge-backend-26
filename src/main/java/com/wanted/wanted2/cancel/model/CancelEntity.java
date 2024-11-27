package com.wanted.wanted2.cancel.model;

import com.wanted.wanted2.order.model.OrderEntity;
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
public class CancelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "cancel_request_amount", nullable = false)
    private BigDecimal cancelAmount;

    @Column(name = "refund_holder")
    private String refundHolder;

    @Column(name = "refund_bank")
    private String refundBank;

    @Column(name = "refund_account")
    private String refundAccount;
}