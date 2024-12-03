package com.wanted.market.domain.transaction.repository;


import com.wanted.market.common.exception.CustomException;
import com.wanted.market.config.TestJpaConfig;
import com.wanted.market.domain.product.Product;
import com.wanted.market.domain.transaction.Transaction;
import com.wanted.market.domain.transaction.TransactionStatus;
import com.wanted.market.domain.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Transaction Repository 테스트")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    private User buyer;
    private User seller;
    private Product product;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        buyer = User.builder()
                .email("buyer@example.com")
                .password("password12345")
                .name("Buyer")
                .build();
        entityManager.persist(buyer);

        seller = User.builder()
                .email("seller@example.com")
                .password("password12345")
                .name("Seller")
                .build();
        entityManager.persist(seller);

        product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(10000))
                .seller(seller)
                .quantity(10)
                .build();
        entityManager.persist(product);

        transaction = Transaction.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .purchasePrice(product.getPrice())
                .build();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("거래 저장 및 초기 상태 확인")
    void saveTransactionAndInitialStatus() {
        // When
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Then
        assertThat(savedTransaction.getId()).isNotNull();
        assertThat(savedTransaction.getStatus()).isEqualTo(TransactionStatus.REQUESTED);
        assertThat(savedTransaction.getPurchasePrice()).isEqualByComparingTo(product.getPrice());
    }

    @Test
    @DisplayName("판매자 승인 후 상태 변경")
    void updateToApprovedStatus() {
        // Given
        Transaction savedTransaction = transactionRepository.save(transaction);
        savedTransaction.updateStatus(TransactionStatus.APPROVED);
        entityManager.flush();
        entityManager.clear();

        // When
        Transaction foundTransaction = transactionRepository.findById(savedTransaction.getId()).orElseThrow();

        // Then
        assertThat(foundTransaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    }

    @Test
    @DisplayName("잘못된 거래 상태 전환 방지")
    void preventInvalidStatusTransition() {
        // Given
        Transaction savedTransaction = transactionRepository.save(transaction);
        // When & Then
        assertThatThrownBy(() -> savedTransaction.updateStatus(TransactionStatus.COMPLETED))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("구매자 확정 후 상태 변경")
    void updateToConfirmedStatus() {
        // Given
        Transaction savedTransaction = transactionRepository.save(transaction);
        savedTransaction.updateStatus(TransactionStatus.APPROVED);
        savedTransaction.updateStatus(TransactionStatus.CONFIRMED);
        entityManager.flush();
        entityManager.clear();

        // When
        Transaction foundTransaction = transactionRepository.findById(savedTransaction.getId()).orElseThrow();

        // Then
        assertThat(foundTransaction.getStatus()).isEqualTo(TransactionStatus.CONFIRMED);
    }

    @Test
    @DisplayName("구매 시점의 가격 유지 확인")
    void maintainPurchasePrice() {
        // Given
        Transaction savedTransaction = transactionRepository.save(transaction);
        BigDecimal originalPrice = savedTransaction.getPurchasePrice();

        // When: 상품 가격 변경
        product.updatePrice(BigDecimal.valueOf(20000));
        entityManager.flush();
        entityManager.clear();

        // Then: 거래의 구매 가격은 변경되지 않음
        Transaction foundTransaction = transactionRepository.findById(savedTransaction.getId()).orElseThrow();
        assertThat(foundTransaction.getPurchasePrice()).isEqualByComparingTo(originalPrice);
        assertThat(foundTransaction.getPurchasePrice()).isNotEqualByComparingTo(product.getPrice());
    }

    @Test
    @DisplayName("구매자의 거래 내역 상태별 조회")
    void findTransactionsByBuyerAndStatus() {
        // Given
        Transaction savedTransaction = transactionRepository.save(transaction);
        savedTransaction.updateStatus(TransactionStatus.APPROVED);

        Transaction anotherTransaction = Transaction.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .purchasePrice(product.getPrice())
                .build();
        transactionRepository.save(anotherTransaction);

        // When
        Page<Transaction> approvedTransactions = transactionRepository.findByBuyerAndStatus(
                buyer,
                TransactionStatus.APPROVED,
                PageRequest.of(0, 10)
        );
        Page<Transaction> requestedTransactions = transactionRepository.findByBuyerAndStatus(
                buyer,
                TransactionStatus.REQUESTED,
                PageRequest.of(0, 10)
        );

        // Then
        assertThat(approvedTransactions).hasSize(1);
        assertThat(requestedTransactions).hasSize(1);
    }

    @Test
    @DisplayName("상품에 대한 거래 진행 중 확인")
    void checkOngoingTransactions() {
        // Given
        transactionRepository.save(transaction);

        // When
        boolean hasOngoingTransactions = transactionRepository.existsByProductAndStatusIn(
                product,
                List.of(TransactionStatus.REQUESTED, TransactionStatus.APPROVED)
        );

        // Then
        assertThat(hasOngoingTransactions).isTrue();
    }

    @Test
    @DisplayName("상품의 완료된 거래 수 확인")
    void countCompletedTransactions() {
        // Given
        Transaction savedTransaction = transactionRepository.save(transaction);
        savedTransaction.updateStatus(TransactionStatus.APPROVED);
        savedTransaction.updateStatus(TransactionStatus.CONFIRMED);
        savedTransaction.updateStatus(TransactionStatus.COMPLETED);

        // When
        long completedCount = transactionRepository.countCompletedTransactionsByProduct(
                product,
                TransactionStatus.COMPLETED
        );

        // Then
        assertThat(completedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("구매자별 상품 구매 여부 확인")
    void checkPurchaseByBuyer() {
        // Given
        transactionRepository.save(transaction);

        // When & Then
        assertThat(transactionRepository.existsByProductAndBuyer(product, buyer)).isTrue();
        assertThat(transactionRepository.existsByProductAndBuyer(product, seller)).isFalse();
    }
}
