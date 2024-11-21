package com.market.controller;

import com.market.domain.dto.OrderRequest;
import com.market.domain.entity.Orders;
import com.market.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // TODO : 현재는 로그인이 없으므로 로그인한 사용자의 ID를 못찾으므로 body로 받는다고 가정한다
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody OrderRequest orderRequest) {
        log.info("orderRequest= {}", orderRequest);
        Orders orders = orderService.save(orderRequest);
        if (orders == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
