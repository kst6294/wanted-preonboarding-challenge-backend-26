package com.wanted.payment.service;

import com.wanted.payment.dto.*;
import com.wanted.payment.repository.OrderRepository;
import com.wanted.payment.repository.ProductRepository;
import com.wanted.payment.rqrs.CreateVirtualBankRs;
import com.wanted.payment.schema.Order;
import com.wanted.payment.schema.OrderStatus;
import com.wanted.payment.schema.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentGatewayService pgService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional()
    public void payment(PaymentCompleteDto dto) {
        boolean completed = pgService.paymentCheck(PgPaymentCheckDto.of(dto.getPaymentId()));

        Order order = orderRepository.findById(dto.getOrderId()).orElseThrow();

        if(completed) {
            order.statusChange(OrderStatus.PAYMENT_COMPLETE);
            order.savePaymentId(dto.getPaymentId());
        }

        throw new RuntimeException("결제가 진행되지 않은 상품입니다. 다시 결제가 필요합니다.");
    }

    public CreateVirtualBankRs createVirtualBank(int orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        VirtualAccountInfoDto dto = pgService.createVirtualAccount(new PgVirtualAccountCreateDto(orderId, order.getFinalPrice()));

        return new CreateVirtualBankRs(dto.getPaymentId(), dto.getBankName(), dto.getBankNum(), dto.getBankDate());
    }

    public void paymentCancel(PaymentCancelDto dto) {
        PgPaymentCancelDto pgDto = PgPaymentCancelDto.of(dto.getPaymentId(), dto.isAll());

        if(!dto.isAll()) {
            int price = productRepository.findAllById(dto.getProductIds()).stream().mapToInt(Product::getPrice).sum(); // product id에 해당하는 price 가져오기.
            pgDto.savePrice(price);
        }

        pgService.paymentCancel(pgDto);
    }
}
