package com.wanted.payment.schema;

public class Order {
    private Integer Id;
    private OrderStatus status = OrderStatus.PAYMENT_NOT_COMPLETE;

    public void statusChange(OrderStatus status) {
        this.status = status;
    }
}
