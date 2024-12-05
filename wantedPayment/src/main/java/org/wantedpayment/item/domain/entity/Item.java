package org.wantedpayment.item.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wantedpayment.global.util.BaseEntity;
import org.wantedpayment.member.domain.entity.Member;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Setter
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void decreaseQuantity() {
        this.quantity--;
    }

    public void increaseQuantity() {
        this.quantity++;

        if(this.status != ItemStatus.ON_SALE) {
            this.status = ItemStatus.ON_SALE;
        }
    }

    public void changeStatus(ItemStatus newStatus) {
        this.status = newStatus;
    }

    public void updateItem(String name, String description, BigDecimal price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }
}
