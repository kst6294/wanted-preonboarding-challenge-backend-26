package com.market.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Builder
@ToString(exclude = {"payments"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_uid", length = 100, nullable = false, unique = true)
    private String merchantUid;

    @Column(name = "ordered_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime orderedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "id")
    private Customers seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", referencedColumnName = "id")
    private Customers buyer;

    @OneToOne(mappedBy = "orderId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private OrderProducts orderProducts;

    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Payments payments;
}
