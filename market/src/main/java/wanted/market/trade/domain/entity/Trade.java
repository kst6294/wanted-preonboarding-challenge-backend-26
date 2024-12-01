package wanted.market.trade.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wanted.market.item.domain.entity.Item;
import wanted.market.member.domain.entity.Member;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trade")
public class Trade {
    @Id
    @GeneratedValue
    @Column(name = "trade_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_status")
    private TradeStatus status;

    @Column(name = "trade_price")
    private int price;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Member buyer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller;

    @Column(name = "merchant_uid")
    private String merchantUid;


    public void setStatusSELL() {
        if (status.equals(TradeStatus.BUY)) {
            this.status = TradeStatus.SELL;
        } else {
            throw new RuntimeException("구매요청이 아닌 상태에서 판매승인 요청");
        }
    }

    public void setStatusEND() {
        if (status.equals(TradeStatus.SELL)) {
            this.status = TradeStatus.END;
        } else {
            throw new RuntimeException("판매승인이 아닌 상태에서 구매 확정 요청");
        }
    }

    public void setStatusCancel() {
        this.status = TradeStatus.REFUSED;
    }

    public Trade setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
        return this;
    }
}
