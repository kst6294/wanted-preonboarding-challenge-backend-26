package com.market.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "imp_uid", length = 100, nullable = false, unique = true)
    private String impUid;

    @Column(name = "merchant_uid", length = 100, nullable = false, unique = true)
    private String merchantUid;

    @Column(nullable = false, columnDefinition = "DECIMAL(65, 0)")
    private BigInteger amount;

    @Column(name = "pay_method", length = 10)
    private String payMethod;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(name = "paid_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime paidAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Orders orders;
}
