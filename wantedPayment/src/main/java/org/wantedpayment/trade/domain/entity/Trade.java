package org.wantedpayment.trade.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wantedpayment.global.util.BaseEntity;
import org.wantedpayment.item.domain.entity.Item;
import org.wantedpayment.member.domain.entity.Member;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trade extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal tradePrice;
    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus;
    private LocalDateTime paymentDateTime;
    private String orderNumber;

    @Setter
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Setter
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Member seller;

    @Setter
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    public void acceptTrade() {
        this.tradeStatus = TradeStatus.ACCEPTED;
    }

    public void confirmPurchase() {
        this.tradeStatus = TradeStatus.CONFIRMED;
    }

    public void cancelPurchase() {
        this.tradeStatus = TradeStatus.CANCELED;
    }

    public void refusePurchase() {
        this.tradeStatus = TradeStatus.REFUSED;
    }
}
