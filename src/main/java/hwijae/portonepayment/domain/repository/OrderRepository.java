package hwijae.portonepayment.domain.repository;

import hwijae.portonepayment.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {


}
