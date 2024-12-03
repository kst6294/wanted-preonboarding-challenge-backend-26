package com.wanted.market.domain.product;

import com.wanted.market.domain.base.BaseEntity;
import com.wanted.market.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_status", columnList = "status"),
                @Index(name = "idx_seller", columnList = "seller_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Min(0)
    @Column(nullable = false)
    private Integer quantity;

    @Version
    private Long version;

    @Builder
    private Product(String name, BigDecimal price, User seller, Integer quantity) {
        validateProductInfo(name, price, seller, quantity);

        this.name = name;
        this.price = price;
        this.seller = seller;
        this.status = ProductStatus.ON_SALE;
        this.quantity = quantity;
    }

    public void updateStatus(ProductStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }

    public void updateQuantity(Integer quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
        updateStatusByQuantity();
    }

    public boolean isSellerMatch(User user) {
        if (user == null) {
            return false;
        }
        return this.seller.getId().equals(user.getId());
    }

    public boolean canPurchase() {
        return this.status == ProductStatus.ON_SALE && this.quantity > 0;
    }

    public void decreaseQuantity() {
        if (this.quantity <= 0) {
            throw new IllegalStateException("No items available for purchase");
        }
        this.quantity--;
        updateStatusByQuantity();
    }

    private void validateProductInfo(String name, BigDecimal price, User seller, Integer quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be empty");
        }
        validatePrice(price);
        if (seller == null) {
            throw new IllegalArgumentException("Seller must not be null");
        }
        validateQuantity(quantity);
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be zero or positive");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    private void updateStatusByQuantity() {
        if (this.quantity > 0) {
            this.status = ProductStatus.ON_SALE;
        } else {
            this.status = ProductStatus.COMPLETED;
        }
    }

    public void updatePrice(BigDecimal price) {
        validatePrice(price);
        this.price = price;
    }
}
