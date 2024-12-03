package com.wanted.market.domain.product;

import com.wanted.market.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Product 도메인 테스트")
class ProductTest {
    private Long sequence;

    @BeforeEach
    void setUp() {
        sequence = 1L;
    }

    @Test
    @DisplayName("상품 생성 시 기본 상태는 ON_SALE이다")
    void createProduct() {
        // given
        User seller = createTestUser();
        String name = "테스트 상품";
        BigDecimal price = BigDecimal.valueOf(10000);
        Integer quantity = 1;

        // when
        Product product = Product.builder()
                .name(name)
                .price(price)
                .seller(seller)
                .quantity(quantity)
                .build();

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("상품 수량이 0이 되면 COMPLETED 상태가 된다")
    void updateStatusWhenQuantityIsZero() {
        // given
        Product product = createTestProduct(1);

        // when
        product.decreaseQuantity();

        // then
        assertThat(product.getQuantity()).isZero();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.COMPLETED);
    }

    @Test
    @DisplayName("수량이 없는 상품은 구매할 수 없다")
    void cannotPurchaseWhenNoQuantity() {
        // given
        Product product = createTestProduct(0);

        // then
        assertThat(product.canPurchase()).isFalse();
    }

    @Test
    @DisplayName("재고가 있어도 RESERVED 상태면 구매할 수 없다")
    void cannotPurchaseWhenStatusIsReserved() {
        // given
        Product product = createTestProduct(5);
        product.updateStatus(ProductStatus.RESERVED);

        // then
        assertThat(product.canPurchase()).isFalse();
    }

    @Test
    @DisplayName("재고가 있어도 COMPLETED 상태면 구매할 수 없다")
    void cannotPurchaseWhenStatusIsCompleted() {
        // given
        Product product = createTestProduct(5);
        product.updateStatus(ProductStatus.COMPLETED);

        // then
        assertThat(product.canPurchase()).isFalse();
    }

    @Test
    @DisplayName("재고 수량을 음수로 설정할 수 없다")
    void cannotSetNegativeQuantity() {
        // given
        Product product = createTestProduct(5);

        // when & then
        assertThatThrownBy(() -> product.updateQuantity(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity cannot be negative");
    }

    @Test
    @DisplayName("재고가 없을 때 감소시키면 예외가 발생한다")
    void shouldThrowExceptionWhenDecreasingEmptyQuantity() {
        // given
        Product product = createTestProduct(0);

        // when & then
        assertThatThrownBy(product::decreaseQuantity)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No items available for purchase");
    }

    @Test
    @DisplayName("가격을 음수로 설정할 수 없다")
    void cannotSetNegativePrice() {
        // given
        Product product = createTestProduct(5);
        BigDecimal negativePrice = BigDecimal.valueOf(-1000);

        // when & then
        assertThatThrownBy(() -> product.updatePrice(negativePrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price must be zero or positive");
    }

    @Test
    @DisplayName("재고 증가 시 자동으로 ON_SALE 상태가 된다")
    void shouldBeOnSaleWhenIncreasingQuantity() {
        // given
        Product product = createTestProduct(0);
        product.updateStatus(ProductStatus.COMPLETED);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.COMPLETED);

        // when
        product.updateQuantity(5);

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(product.getQuantity()).isEqualTo(5);
    }

    private User createTestUser() {
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .name("테스터")
                .build();
        ReflectionTestUtils.setField(user, "id", sequence++);
        return user;
    }

    private Product createTestProduct(int quantity) {
        Product product = Product.builder()
                .name("테스트 상품")
                .price(BigDecimal.valueOf(10000))
                .seller(createTestUser())
                .quantity(quantity)
                .build();
        ReflectionTestUtils.setField(product, "id", sequence++);
        return product;
    }
}
