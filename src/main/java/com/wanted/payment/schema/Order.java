package com.wanted.payment.schema;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Enumerated(EnumType.STRING)
    @Column()
    private OrderStatus status = OrderStatus.PAYMENT_NOT_COMPLETE;

    @Column()
    private int finalPrice;

    @Column()
    private String paymentId;

    // ...

    public void statusChange(OrderStatus status) {
        this.status = status;
    }

    public void savePaymentId(String paymentId) {}
}
