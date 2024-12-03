package com.wanted.market.domain.transaction.service;

import com.wanted.market.common.exception.CustomException;
import com.wanted.market.common.exception.ErrorCode;
import com.wanted.market.domain.product.Product;
import com.wanted.market.domain.product.ProductStatus;
import com.wanted.market.domain.product.repository.ProductRepository;
import com.wanted.market.domain.transaction.Transaction;
import com.wanted.market.domain.transaction.TransactionStatus;
import com.wanted.market.domain.transaction.dto.TransactionCreateRequest;
import com.wanted.market.domain.transaction.dto.TransactionResponse;
import com.wanted.market.domain.transaction.dto.TransactionStatusUpdateRequest;
import com.wanted.market.domain.transaction.repository.TransactionRepository;
import com.wanted.market.domain.user.User;
import com.wanted.market.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("TransactionService 테스트")
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User seller;
    private User buyer;
    private Product product;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        seller = User.builder()
                .email("seller@test.com")
                .password("password")
                .name("Seller")
                .build();
        ReflectionTestUtils.setField(seller, "id", 1L);

        buyer = User.builder()
                .email("buyer@test.com")
                .password("password")
                .name("Buyer")
                .build();
        ReflectionTestUtils.setField(buyer, "id", 2L);

        product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(10000))
                .quantity(5)
                .seller(seller)
                .build();
        ReflectionTestUtils.setField(product, "id", 1L);
        product.updateStatus(ProductStatus.ON_SALE);

        transaction = Transaction.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .purchasePrice(product.getPrice())
                .build();
        ReflectionTestUtils.setField(transaction, "id", 1L);
    }

    @Nested
    @DisplayName("거래 생성 테스트")
    class CreateTransaction {

        @Test
        @DisplayName("성공: 정상적인 거래 생성")
        void createTransaction_Success() {
            // Given
            int initialQuantity = product.getQuantity();
            TransactionCreateRequest request = new TransactionCreateRequest(product.getId());
            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(productRepository.findByIdWithPessimisticLock(product.getId())).thenReturn(Optional.of(product));
            when(transactionRepository.existsByProductAndBuyer(product, buyer)).thenReturn(false);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

            // When
            TransactionResponse response = transactionService.createTransaction(buyer.getEmail(), request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.productId()).isEqualTo(product.getId());
            assertThat(response.buyerId()).isEqualTo(buyer.getId());
            assertThat(response.status()).isEqualTo(TransactionStatus.REQUESTED);
            assertThat(response.purchasePrice()).isEqualTo(product.getPrice());
            assertThat(product.getQuantity()).isEqualTo(initialQuantity - 1);
        }

        @Test
        @DisplayName("실패: 이미 구매한 상품 재구매 시도")
        void createTransaction_DuplicatePurchase() {
            // Given
            TransactionCreateRequest request = new TransactionCreateRequest(product.getId());
            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(productRepository.findByIdWithPessimisticLock(product.getId())).thenReturn(Optional.of(product));
            when(transactionRepository.existsByProductAndBuyer(product, buyer)).thenReturn(true);

            // When & Then
            CustomException exception = assertThrows(CustomException.class,
                    () -> transactionService.createTransaction(buyer.getEmail(), request));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_RESOURCE);
        }

        @Test
        @DisplayName("실패: 재고가 없는 상품 구매 시도")
        void createTransaction_NoStock() {
            // Given
            product.updateQuantity(0);
            TransactionCreateRequest request = new TransactionCreateRequest(product.getId());
            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(productRepository.findByIdWithPessimisticLock(product.getId())).thenReturn(Optional.of(product));
            when(transactionRepository.existsByProductAndBuyer(product, buyer)).thenReturn(false);

            // When & Then
            CustomException exception = assertThrows(CustomException.class,
                    () -> transactionService.createTransaction(buyer.getEmail(), request));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_AVAILABLE);
        }
    }

    @Nested
    @DisplayName("거래 상태 업데이트 테스트")
    class UpdateTransactionStatus {

        @Test
        @DisplayName("성공: 판매자의 거래 승인")
        void updateStatus_SellerApproval() {
            // Given
            TransactionStatusUpdateRequest request = new TransactionStatusUpdateRequest(TransactionStatus.APPROVED);
            when(userRepository.findByEmail(seller.getEmail())).thenReturn(Optional.of(seller));
            when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

            // When
            TransactionResponse response = transactionService.updateTransactionStatus(
                    seller.getEmail(),
                    transaction.getId(),
                    request
            );

            // Then
            assertThat(response.status()).isEqualTo(TransactionStatus.APPROVED);
        }

        @Test
        @DisplayName("성공: 구매자의 구매 확정")
        void updateStatus_BuyerConfirmation() {
            // Given
            transaction.updateStatus(TransactionStatus.APPROVED);
            TransactionStatusUpdateRequest request = new TransactionStatusUpdateRequest(TransactionStatus.CONFIRMED);
            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

            // When
            TransactionResponse response = transactionService.updateTransactionStatus(
                    buyer.getEmail(),
                    transaction.getId(),
                    request
            );

            // Then
            assertThat(response.status()).isEqualTo(TransactionStatus.CONFIRMED);
        }

        @Test
        @DisplayName("실패: 잘못된 상태 변경 시도")
        void updateStatus_InvalidStatusTransition() {
            // Given
            TransactionStatusUpdateRequest request = new TransactionStatusUpdateRequest(TransactionStatus.COMPLETED);
            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

            // When & Then
            CustomException exception = assertThrows(CustomException.class,
                    () -> transactionService.updateTransactionStatus(buyer.getEmail(), transaction.getId(), request));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_STATUS_UPDATE);
        }
    }

    @Nested
    @DisplayName("거래 조회 테스트")
    class GetTransactions {

        @Test
        @DisplayName("성공: 구매자의 거래 내역 조회")
        void getPurchasedTransactions() {
            // Given
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));

            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(transactionRepository.findByBuyerAndStatus(buyer, TransactionStatus.COMPLETED, pageRequest))
                    .thenReturn(transactionPage);

            // When
            Page<TransactionResponse> response = transactionService.getPurchasedTransactions(buyer.getEmail(), pageRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).buyerId()).isEqualTo(buyer.getId());
            assertThat(response.getContent().get(0).purchasePrice()).isEqualTo(product.getPrice());
        }

        @Test
        @DisplayName("성공: 진행 중인 거래 내역 조회")
        void getOngoingTransactions() {
            // Given
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
            List<TransactionStatus> ongoingStatuses = List.of(
                    TransactionStatus.REQUESTED,
                    TransactionStatus.APPROVED
            );

            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(transactionRepository.findRecentTransactionsByUser(buyer, ongoingStatuses, pageRequest))
                    .thenReturn(transactionPage);

            // When
            Page<TransactionResponse> response = transactionService.getOngoingTransactions(buyer.getEmail(), pageRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).purchasePrice()).isEqualTo(product.getPrice());
        }
    }

    @Nested
    @DisplayName("상품 상태 업데이트 테스트")
    class UpdateProductStatus {

        @Test
        @DisplayName("성공: 수량이 남아있을 때는 ON_SALE 상태로 변경")
        void updateProductStatus_RemainingQuantity() {
            // Given
            product.updateQuantity(3);
            TransactionCreateRequest request = new TransactionCreateRequest(product.getId());
            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(productRepository.findByIdWithPessimisticLock(product.getId())).thenReturn(Optional.of(product));
            when(transactionRepository.existsByProductAndBuyer(product, buyer)).thenReturn(false);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

            // When
            transactionService.createTransaction(buyer.getEmail(), request);

            // Then
            assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
            assertThat(product.getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("성공: 수량이 0이고 진행 중인 거래가 있을 때는 RESERVED 상태로 변경")
        void updateProductStatus_NoQuantityWithOngoingTransactions() {
            // Given
            product.updateQuantity(1);
            TransactionCreateRequest request = new TransactionCreateRequest(product.getId());
            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(productRepository.findByIdWithPessimisticLock(product.getId())).thenReturn(Optional.of(product));
            when(transactionRepository.existsByProductAndBuyer(product, buyer)).thenReturn(false);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
            when(transactionRepository.existsByProductAndStatusIn(eq(product), any())).thenReturn(true);

            // When
            transactionService.createTransaction(buyer.getEmail(), request);

            // Then
            assertThat(product.getStatus()).isEqualTo(ProductStatus.RESERVED);
            assertThat(product.getQuantity()).isEqualTo(0);
        }

        @Test
        @DisplayName("성공: 수량이 0이고 진행 중인 거래가 없을 때는 COMPLETED 상태로 변경")
        void updateProductStatus_NoQuantityNoOngoingTransactions() {
            // Given
            product.updateQuantity(1);
            TransactionCreateRequest request = new TransactionCreateRequest(product.getId());
            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(productRepository.findByIdWithPessimisticLock(product.getId())).thenReturn(Optional.of(product));
            when(transactionRepository.existsByProductAndBuyer(product, buyer)).thenReturn(false);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
            when(transactionRepository.existsByProductAndStatusIn(eq(product), any())).thenReturn(false);

            // When
            transactionService.createTransaction(buyer.getEmail(), request);

            // Then
            assertThat(product.getStatus()).isEqualTo(ProductStatus.COMPLETED);
            assertThat(product.getQuantity()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("구매 가격 보존 테스트")
    class PurchasePricePreservation {

        @Test
        @DisplayName("성공: 구매 시점의 가격이 보존되어야 함")
        void preservePurchasePrice() {
            // Given
            BigDecimal originalPrice = product.getPrice();
            BigDecimal newPrice = BigDecimal.valueOf(20000);
            TransactionCreateRequest request = new TransactionCreateRequest(product.getId());

            when(userRepository.findByEmail(buyer.getEmail())).thenReturn(Optional.of(buyer));
            when(productRepository.findByIdWithPessimisticLock(product.getId())).thenReturn(Optional.of(product));
            when(transactionRepository.existsByProductAndBuyer(product, buyer)).thenReturn(false);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

            // When
            TransactionResponse response = transactionService.createTransaction(buyer.getEmail(), request);
            product.updatePrice(newPrice);  // 가격 변경

            // Then
            assertThat(response.purchasePrice()).isEqualTo(originalPrice);
            assertThat(product.getPrice()).isEqualTo(newPrice);
        }
    }
}
