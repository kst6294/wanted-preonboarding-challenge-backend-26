package com.wanted.wanted2.payment.service.impl;

import com.wanted.wanted2.order.model.OrderDto;
import com.wanted.wanted2.order.repository.OrderRepository;
import com.wanted.wanted2.order.service.OrderService;
import com.wanted.wanted2.cancel.model.CancelDto;
import com.wanted.wanted2.cancel.model.CancelEntity;
import com.wanted.wanted2.payment.model.PaymentDto;
import com.wanted.wanted2.payment.model.PaymentEntity;
import com.wanted.wanted2.payment.repository.PaymentRepository;
import com.wanted.wanted2.payment.service.PaymentService;
import com.wanted.wanted2.product.model.ProductEntity;
import com.wanted.wanted2.product.model.Status;
import com.wanted.wanted2.product.repository.ProductRepository;
import com.wanted.wanted2.users.model.UserDetail;
import com.wanted.wanted2.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> findByUser(UserDetail userDetail) {
        return isUserAuthorized(userDetail)
                .map(authorized -> {
                    List<PaymentEntity> payments = paymentRepository.findAllByBuyer(userDetail.getUser().getId());
                    if (payments.isEmpty()) {
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(payments);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Override
    public ResponseEntity<PaymentEntity> findById(UserDetail userDetail, String id) {
        return ResponseEntity.ok(paymentRepository.findById(id).orElse(null));
    }

    @Override
    public ResponseEntity<PaymentEntity> save(UserDetail userDetail, PaymentDto payment) {
        PaymentEntity paymentEntity = paymentRepository.save(PaymentEntity.builder()
                .impUid(payment.getImpUid())
                .merchantUid(payment.getMerchantUid())
                .amount(payment.getAmount())
                .product(productRepository.findById(payment.getId()).get().getName())
                .buyer(userRepository.findById(payment.getBuyerId()).get())
                .build());

        if (paymentEntity != null) {
            ProductEntity productEntity = productRepository.findById(payment.getProductId()).get();
            productEntity.setStatus(Status.RESERVED);
            productRepository.save(productEntity);

            orderService.save(userDetail, OrderDto.builder()
                    .orderNo(payment.getMerchantUid())
                    .paymentId(paymentEntity.getImpUid())
                    .productId(productEntity.getId())
                    .build());
            return ResponseEntity.ok(paymentEntity);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private Optional<UserDetail> isUserAuthorized(UserDetail userDetail) {
        return Optional.ofNullable(userDetail)
                .filter(detail -> !detail.getAuthorities().isEmpty());
    }
}
