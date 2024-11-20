package hwijae.portonepayment.domain.service;


import hwijae.portonepayment.domain.entity.Member;
import hwijae.portonepayment.domain.entity.Order;

public interface OrderService {
    Order autoOrder(Member member); // 자동 주문
}
