package com.wanted.wanted2.order.service;

import com.wanted.wanted2.order.model.OrderDto;
import com.wanted.wanted2.order.model.OrderEntity;
import com.wanted.wanted2.users.model.UserDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {

    ResponseEntity<List<OrderEntity>> findByUser(UserDetail userDetail);

    ResponseEntity<OrderEntity> findById(UserDetail userDetail, Long id);

    ResponseEntity<OrderEntity> save(UserDetail userDetail, OrderDto order);
}
