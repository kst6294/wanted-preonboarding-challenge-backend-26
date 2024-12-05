package com.wanted.wanted2.order.repository;

import com.wanted.wanted2.order.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, QOrderRepository {
}
