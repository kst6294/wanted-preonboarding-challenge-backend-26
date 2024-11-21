package com.market.repository;

import com.market.domain.entity.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProducts, Long> {
}
