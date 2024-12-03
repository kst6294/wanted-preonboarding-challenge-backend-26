package com.wanted.market.domain.product.repository;

import com.wanted.market.config.TestJpaConfig;
import com.wanted.market.domain.product.Product;
import com.wanted.market.domain.product.ProductStatus;
import com.wanted.market.domain.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Product Repository 테스트")
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    private User seller;
    private Product product;

    @BeforeEach
    void setUp() {
        // Given: 판매자와 상품 설정
        seller = User.builder()
                .email("seller@example.com")
                .password("password")
                .name("Seller")
                .build();
        entityManager.persist(seller);

        product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(10000))
                .seller(seller)
                .quantity(10)
                .build();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("상품 저장 성공")
    void saveProduct() {
        // When
        Product savedProduct = productRepository.save(product);

        // Then
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(savedProduct.getQuantity()).isEqualTo(10);
        assertThat(savedProduct.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    @DisplayName("상품 상태로 조회")
    void findByStatus() {
        // Given
        productRepository.save(product);

        // When
        Page<Product> products = productRepository.findByStatus(
                ProductStatus.ON_SALE,
                PageRequest.of(0, 10)
        );

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.getContent().get(0).getStatus())
                .isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    @DisplayName("판매자로 상품 조회")
    void findBySeller() {
        // Given
        productRepository.save(product);

        // When
        Page<Product> products = productRepository.findBySeller(
                seller,
                PageRequest.of(0, 10)
        );

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.getContent().get(0).getSeller().getId())
                .isEqualTo(seller.getId());
    }

    @Test
    @DisplayName("Optimistic Lock 테스트 - 동시 수정 시 예외 발생")
    void optimisticLockTest() throws InterruptedException {
        // Given
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // When & Then
        executorService.submit(() -> {
            try {
                productRepository.findByIdWithOptimisticLock(productId)
                        .ifPresent(p -> {
                            p.updateQuantity(5);
                            productRepository.save(p);
                        });
            } finally {
                latch.countDown();
            }
        });

        executorService.submit(() -> {
            try {
                Thread.sleep(100); // 첫 번째 트랜잭션이 먼저 실행되도록 지연
                assertThatThrownBy(() -> productRepository.findByIdWithOptimisticLock(productId)
                        .ifPresent(p -> {
                            p.updateQuantity(3);
                            productRepository.save(p);
                        })).isInstanceOf(OptimisticLockingFailureException.class);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new AssertionError("Test timed out");
        }
        executorService.shutdown();
    }

    @Test
    @DisplayName("이름으로 상품 검색")
    void findByNameContaining() {
        // Given
        productRepository.save(product);

        // When
        Page<Product> products = productRepository.findByNameContaining(
                "Test",
                PageRequest.of(0, 10)
        );

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.getContent().get(0).getName())
                .contains("Test");
    }

    @Test
    @DisplayName("판매 가능한 상품만 조회")
    void findAvailableProducts() {
        // Given
        productRepository.save(product);
        Product soldOutProduct = Product.builder()
                .name("Sold Out Product")
                .price(BigDecimal.valueOf(20000))
                .seller(seller)
                .quantity(0)
                .build();
        productRepository.save(soldOutProduct);

        // When
        Page<Product> products = productRepository.findAvailableProducts(
                ProductStatus.ON_SALE,
                PageRequest.of(0, 10)
        );

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.getContent().get(0).getQuantity()).isGreaterThan(0);
        assertThat(products.getContent().get(0).getStatus())
                .isEqualTo(ProductStatus.ON_SALE);
    }
}
