package com.wanted.wanted2.cancel.service.impl;

import com.wanted.wanted2.cancel.model.CancelDto;
import com.wanted.wanted2.cancel.model.CancelEntity;
import com.wanted.wanted2.cancel.repository.CancelRepository;
import com.wanted.wanted2.cancel.service.CancelService;
import com.wanted.wanted2.order.repository.OrderRepository;
import com.wanted.wanted2.payment.repository.PaymentRepository;
import com.wanted.wanted2.users.model.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CancelServiceImpl implements CancelService {
    private final CancelRepository cancelRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public ResponseEntity<CancelEntity> save(UserDetail userDetail, CancelDto cancel) {
        return isUserAuthorized(userDetail)
                .map(user -> orderRepository.findById(cancel.getOrderId())
                        .filter(order -> paymentRepository.existsById(order.getPayment().getImpUid()))
                        .map(order -> cancelRepository.save(
                                CancelEntity.builder()
                                        .reason(cancel.getReason())
                                        .order(order)
                                        .cancelAmount(cancel.getCancelAmount())
                                        .refundHolder(cancel.getRefundHolder())
                                        .refundBank(cancel.getRefundBank())
                                        .refundAccount(cancel.getRefundAccount())
                                        .build()))
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private Optional<UserDetail> isUserAuthorized(UserDetail userDetail) {
        return Optional.ofNullable(userDetail)
                .filter(detail -> !detail.getAuthorities().isEmpty());
    }
}
