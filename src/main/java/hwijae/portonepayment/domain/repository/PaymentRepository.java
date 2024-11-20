package hwijae.portonepayment.domain.repository;

import hwijae.portonepayment.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
