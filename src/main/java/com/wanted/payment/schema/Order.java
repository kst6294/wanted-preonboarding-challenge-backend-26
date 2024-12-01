package com.wanted.payment.schema;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Enumerated(EnumType.STRING)
    @Column()
    private OrderStatus status = OrderStatus.PAYMENT_NOT_COMPLETE;

    // ...

    public void statusChange(OrderStatus status) {
        this.status = status;
    }
}
