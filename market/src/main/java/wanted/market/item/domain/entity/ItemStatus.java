package wanted.market.item.domain.entity;

public enum ItemStatus {
    //판매중, 예약중, 완료

    ONSALE("판매중"),
    BOOKED("예약중"),
    COMPLETED("완료");

    private final String status;

    public String getStatus() {
        return status;
    }

    private ItemStatus(String status) {
        this.status = status;
    }
}
