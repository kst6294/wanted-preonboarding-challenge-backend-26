package com.wanted.wanted2.payment.repository;

import com.wanted.wanted2.payment.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
    List<PaymentEntity> findAllByBuyer(Long userId);
}
