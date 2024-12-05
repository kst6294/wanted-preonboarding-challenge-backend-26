package com.wanted.wanted2.order.repository;

import com.wanted.wanted2.order.model.OrderEntity;

import java.util.List;

public interface QOrderRepository {
    List<OrderEntity> findByUser(Long userId);
}
