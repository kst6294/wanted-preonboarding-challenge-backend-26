package com.wanted.payment.repository;

import com.wanted.payment.schema.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
