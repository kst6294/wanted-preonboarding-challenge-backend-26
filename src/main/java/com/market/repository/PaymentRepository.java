package com.market.repository;

import com.market.domain.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payments, Long>, QPaymentRepository {
}
