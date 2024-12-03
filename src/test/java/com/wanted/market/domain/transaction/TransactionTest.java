package com.wanted.market.domain.transaction;

import com.wanted.market.common.exception.CustomException;
import com.wanted.market.domain.product.Product;
import com.wanted.market.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Transaction 도메인 테스트")
class TransactionTest {
    private Long sequence;

    @BeforeEach
    void setUp() {
        sequence = 1L;
    }

    @Test
    @DisplayName("거래 생성 시 기본 상태는 REQUESTED이다")
    void createTransaction() {
        // given
        Product product = createTestProduct(1);
        User buyer = createTestUser("buyer@test.com");
        User seller = createTestUser("seller@test.com");
        BigDecimal purchasePrice = BigDecimal.valueOf(10000);

        // when
        Transaction transaction = Transaction.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .purchasePrice(purchasePrice)
                .build();

        // then
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.REQUESTED);
        assertThat(transaction.getPurchasePrice()).isEqualTo(purchasePrice);
    }

    @Test
    @DisplayName("구매자와 판매자가 동일하면 거래를 생성할 수 없다")
    void cannotCreateTransactionWithSameBuyerAndSeller() {
        // given
        User user = createTestUser("user@test.com");
        Product product = createTestProduct(1);
        BigDecimal purchasePrice = BigDecimal.valueOf(10000);

        // when & then
        assertThatThrownBy(() ->
                Transaction.builder()
                        .product(product)
                        .buyer(user)
                        .seller(user)
                        .purchasePrice(purchasePrice)
                        .build()
        ).isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("같은 제품에 대해 여러 사용자가 구매할 수 있다")
    void multipleUsersCanPurchaseSameProduct() {
        // given
        Product product = createTestProduct(3); // 3개 수량의 상품
        User buyer1 = createTestUser("buyer1@test.com");
        User buyer2 = createTestUser("buyer2@test.com");
        BigDecimal purchasePrice = BigDecimal.valueOf(10000);

        // when
        Transaction transaction1 = Transaction.builder()
                .product(product)
                .buyer(buyer1)
                .seller(product.getSeller())
                .purchasePrice(purchasePrice)
                .build();

        Transaction transaction2 = Transaction.builder()
                .product(product)
                .buyer(buyer2)
                .seller(product.getSeller())
                .purchasePrice(purchasePrice)
                .build();

        // then
        assertThat(transaction1.getBuyer()).isEqualTo(buyer1);
        assertThat(transaction2.getBuyer()).isEqualTo(buyer2);
        assertThat(transaction1.getProduct()).isEqualTo(product);
        assertThat(transaction2.getProduct()).isEqualTo(product);
    }

    @Test
    @DisplayName("거래 상태는 순차적으로만 변경 가능하다")
    void transactionStatusShouldChangeSequentially() {
        // given
        Transaction transaction = createTestTransaction();

        // when & then
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.REQUESTED);

        transaction.updateStatus(TransactionStatus.APPROVED);
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);

        transaction.updateStatus(TransactionStatus.CONFIRMED);
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.CONFIRMED);

        transaction.updateStatus(TransactionStatus.COMPLETED);
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
    }

    @Test
    @DisplayName("잘못된 거래 상태 변경을 시도하면 예외가 발생한다")
    void shouldThrowExceptionOnInvalidStatusTransition() {
        // given
        Transaction transaction = createTestTransaction();

        // when & then
        assertThatThrownBy(() ->
                transaction.updateStatus(TransactionStatus.COMPLETED)
        ).isInstanceOf(CustomException.class)
                .hasMessageContaining("잘못된 거래 상태 변경입니다");
    }

    @Test
    @DisplayName("구매 가격은 0보다 커야 한다")
    void purchasePriceShouldBePositive() {
        // given
        Product product = createTestProduct(1);
        User buyer = createTestUser("buyer@test.com");
        BigDecimal invalidPrice = BigDecimal.ZERO;

        // when & then
        assertThatThrownBy(() ->
                Transaction.builder()
                        .product(product)
                        .buyer(buyer)
                        .seller(product.getSeller())
                        .purchasePrice(invalidPrice)
                        .build()
        ).isInstanceOf(CustomException.class);
    }

    private User createTestUser(String email) {
        User user = User.builder()
                .email(email)
                .password("testPassword123!@")
                .name("테스트유저")
                .build();
        ReflectionTestUtils.setField(user, "id", sequence++);
        return user;
    }

    private Product createTestProduct(int quantity) {
        User seller = createTestUser("seller@test.com");
        return createTestProduct(seller, quantity);
    }

    private Product createTestProduct(User seller, int quantity) {
        Product product = Product.builder()
                .name("테스트상품")
                .price(BigDecimal.valueOf(10000))
                .seller(seller)
                .quantity(quantity)
                .build();
        ReflectionTestUtils.setField(product, "id", sequence++);
        return product;
    }

    private Transaction createTestTransaction() {
        Product product = createTestProduct(1);
        User buyer = createTestUser("buyer@test.com");
        return Transaction.builder()
                .product(product)
                .buyer(buyer)
                .seller(product.getSeller())
                .purchasePrice(BigDecimal.valueOf(10000))
                .build();
    }
}
