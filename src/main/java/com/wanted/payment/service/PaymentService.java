package com.wanted.payment.service;

import com.wanted.payment.dto.PaymentCheckDto;
import com.wanted.payment.dto.PaymentCompleteDto;
import com.wanted.payment.dto.VirtualAccountCreateDto;
import com.wanted.payment.dto.VirtualAccountInfoDto;
import com.wanted.payment.repository.OrderRepository;
import com.wanted.payment.repository.ProductRepository;
import com.wanted.payment.rqrs.CreateVirtualBankRs;
import com.wanted.payment.schema.Order;
import com.wanted.payment.schema.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentGatewayService pgService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public void payment(PaymentCompleteDto dto) {
        boolean completed = pgService.paymentCheck(PaymentCheckDto.of(dto.getPaymentId()));

        Order order = orderRepository.findById(dto.getOrderId()).orElseThrow();

        if(completed) {
            order.statusChange(OrderStatus.PAYMENT_COMPLETE);
        }

        throw new RuntimeException("결제가 진행되지 않은 상품입니다. 다시 결제가 필요합니다.");
    }

    public CreateVirtualBankRs createVirtualBank(int orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        VirtualAccountInfoDto dto = pgService.createVirtualAccount(new VirtualAccountCreateDto(orderId, order.getFinalPrice()));

        return new CreateVirtualBankRs(dto.getPaymentId(), dto.getBankName(), dto.getBankNum(), dto.getBankDate());
    }

    public void paymentCancel() {
        // 취소할 주문
        // 취소할 상품 목록

        // 포트원 거래 고유 번호,

    }
}
