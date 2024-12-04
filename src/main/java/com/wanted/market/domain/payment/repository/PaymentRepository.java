package com.wanted.market.domain.payment.repository;

import com.wanted.market.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wanted.market.domain.transaction.Transaction;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByMerchantUid(String merchantUid);
    Optional<Payment> findByTransaction(Transaction transaction);
}
