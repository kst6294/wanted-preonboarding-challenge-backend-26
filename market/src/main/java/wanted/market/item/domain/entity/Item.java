package wanted.market.item.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wanted.market.member.domain.entity.Member;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "item")
public class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String itemName;

    private int price;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member seller;

    public Item setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public Item setPrice(int price) {
        this.price = price;
        return this;
    }

    public Item setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public void itemQuantityDecrease() {
        quantity -= 1;
        if (quantity < 0) {
            throw new RuntimeException("수량이 부족하여 구매요청이 불가");
        } else if (quantity == 0) {
            this.status = ItemStatus.BOOKED;
        } else {
            this.status = ItemStatus.ONSALE;
        }
    }

    public void setStatusCOMPLETE() {
        if (quantity == 0) {
            this.status = ItemStatus.COMPLETED;
        } else if (quantity > 0) {
            this.status = ItemStatus.ONSALE;
        } else {
            throw new RuntimeException("수량이 음수");
        }
    }




}
