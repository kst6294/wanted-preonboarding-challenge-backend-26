package com.market.service;

import com.market.domain.entity.*;
import com.market.repository.CustomerRepository;
import com.market.repository.OrderProductRepository;
import com.market.repository.OrderRepository;
import com.market.repository.ProductRepository;
import com.market.request.OrderRequest;
import com.market.response.SimpleOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    public Orders save(OrderRequest orderRequest) {
        Customers seller = findCustomerById(orderRequest.sellerId());
        Customers buyer = findCustomerById(orderRequest.buyerId());

        Products products = productRepository.findById(orderRequest.productId())
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다."));

        Orders orders = Orders.builder()
                .merchantUid(orderRequest.merchantUid())
                .orderedAt(LocalDateTime.now())
                .seller(seller)
                .buyer(buyer)
                .build();
        Orders savedOrders = orderRepository.save(orders);
        log.info("save savedOrders= {}", savedOrders);

        OrderProducts orderProducts = OrderProducts.builder()
                .amount(orderRequest.amount())
                .reservationStatus(ReservationStatus.RESERVATION)
                .orderId(savedOrders)
                .productId(products)
                .build();
        orderProductRepository.save(orderProducts);

        return savedOrders;
    }

    private Customers findCustomerById(Long customerId) {
        log.info("findCustomerById= {}", customerId);
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("고객을 찾을 수 없습니다."));
    }

    public SimpleOrderResponse findByMerchantUid(String merchantUid) {
        SimpleOrderResponse order = orderRepository.findByMerchantUid(merchantUid);
        if (order == null) {
            throw new IllegalArgumentException("주문을 찾을 수 없습니다.");
        }
        log.info("findByMerchantUid order= {}", order);

        return order;
    }

    @Transactional
    public void updateReservationDone(Long id) {
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
        order.getOrderProducts().setReservationStatus(ReservationStatus.DONE);
    }

    public void deleteOrder(String merchantUid) {
        SimpleOrderResponse order = orderRepository.findByMerchantUid(merchantUid);
        if (order == null) {
            throw new IllegalArgumentException("존재하지 않는 주문입니다.");
        }

        orderRepository.deleteById(order.id());
    }
}
