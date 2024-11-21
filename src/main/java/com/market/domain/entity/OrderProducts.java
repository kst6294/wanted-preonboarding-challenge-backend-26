package com.market.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigInteger;

@Entity
@Table(name = "order_products")
@Getter
public class OrderProducts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "DECIMAL(65, 0)")
    private BigInteger amount;

    @Column(name = "reservation_status", nullable = false, columnDefinition = "VARCHAR(20)")
    private ReservationStatus reservationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Orders orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Products productId;
}
