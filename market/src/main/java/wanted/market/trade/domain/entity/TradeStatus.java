package wanted.market.trade.domain.entity;

public enum TradeStatus {
    //판매중 -> 구매요청 -> 판매승인 = 거래 완료
    //판매중 -> 구매요청 -> 판매승인 -> 구매확정 = 거래 완료

    BUY("구매요청"),
    SELL("판매승인"),
    END("구매확정"),
    REFUSED("취소");

    private final String status;

    public String getStatus() {
        return status;
    }

    private TradeStatus(String status) {
        this.status = status;
    }
}
