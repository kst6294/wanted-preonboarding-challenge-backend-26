package hwijae.portonepayment.domain.repository;

import hwijae.portonepayment.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o" +
            " left join fetch o.payment p" +
            " left join fetch o.member m" +
            " where o.orderUid = :orderUid")
    Optional<Order> findOrderAndPaymentAndMember(String orderUid);

    @Query("select o from Order o" +
            " left join fetch o.payment p" +
            " where o.orderUid = :orderUid")
    Optional<Order> findOrderAndPayment(String orderUid);

    // 회원 ID를 기준으로 주문 목록을 조회 (결제 상태나 상품명 등을 포함한 주문 정보)
    List<Order> findAllByMemberId(Long memberId);

    // 주문 번호(orderUid)로 주문을 조회 (결제 취소 시 사용)
    Optional<Order> findByOrderUid(String orderUid);
}
