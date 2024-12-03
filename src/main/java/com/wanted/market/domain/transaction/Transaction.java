package com.wanted.market.domain.transaction;

import com.wanted.market.common.exception.CustomException;
import com.wanted.market.common.exception.ErrorCode;
import com.wanted.market.domain.base.BaseEntity;
import com.wanted.market.domain.product.Product;
import com.wanted.market.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_product", columnList = "product_id"),
                @Index(name = "idx_buyer", columnList = "buyer_id"),
                @Index(name = "idx_seller", columnList = "seller_id"),
                @Index(name = "idx_transaction_status", columnList = "status")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal purchasePrice;

    @Version
    private Long version;

    @Builder
    private Transaction(Product product, User buyer, User seller, BigDecimal purchasePrice) {
        validateParticipants(product, buyer, seller);
        validatePrice(purchasePrice);

        this.product = product;
        this.buyer = buyer;
        this.seller = seller;
        this.purchasePrice = purchasePrice;
        this.status = TransactionStatus.REQUESTED;
    }

    public void updateStatus(TransactionStatus newStatus) {
        if (newStatus == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        switch (this.status) {
            case REQUESTED:
                if (newStatus != TransactionStatus.APPROVED) {
                    throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
                }
                break;

            case APPROVED:
                if (newStatus != TransactionStatus.CONFIRMED) {
                    throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
                }
                break;

            case CONFIRMED:
                if (newStatus != TransactionStatus.COMPLETED) {
                    throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
                }
                break;

            case COMPLETED:
                throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
        }

        this.status = newStatus;
    }

    public boolean isBuyerMatch(User user) {
        if (user == null) {
            return false;
        }
        return this.buyer.getId().equals(user.getId());
    }

    public boolean isSellerMatch(User user) {
        if (user == null) {
            return false;
        }
        return this.seller.getId().equals(user.getId());
    }

    private void validateParticipants(Product product, User buyer, User seller) {
        if (product == null || buyer == null || seller == null) {
            throw new IllegalArgumentException("Product, buyer, and seller must not be null");
        }

        if (buyer.getId().equals(seller.getId())) {
            throw new IllegalArgumentException("Buyer and seller cannot be the same user");
        }
    }

    private void validatePrice(BigDecimal purchasePrice) {
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Purchase price must be positive");
        }
    }
}
