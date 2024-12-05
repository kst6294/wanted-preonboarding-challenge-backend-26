package hwijae.portonepayment.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long price;
    private String itemName;
    private String orderUid; // 주문 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Builder
    public Order(Long price, String itemName, String orderUid, Member member, Payment payment) {
        this.price = price;
        this.itemName = itemName;
        this.orderUid = orderUid;
        this.member = member;
        this.payment = payment;
    }
}
