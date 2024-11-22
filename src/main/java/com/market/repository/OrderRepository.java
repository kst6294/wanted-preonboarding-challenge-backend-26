package com.market.repository;

import com.market.domain.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long>, QOrderRepository {

}
