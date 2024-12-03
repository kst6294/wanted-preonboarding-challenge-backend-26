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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 거래를 생성합니다. (구매 요청)
     * 요구사항 5, 10: 구매하기 버튼으로 거래 시작, 1인당 1개 제한
     */
    @Transactional
    public TransactionResponse createTransaction(String email, TransactionCreateRequest request) {
        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findByIdWithPessimisticLock(request.productId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        validateTransactionCreation(buyer, product);

        Transaction transaction = Transaction.builder()
                .product(product)
                .buyer(buyer)
                .seller(product.getSeller())
                .purchasePrice(product.getPrice())
                .build();

        product.decreaseQuantity();
        updateProductStatus(product);

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    /**
     * 거래 상태를 업데이트합니다.
     * 요구사항 8, 11: 판매자의 판매 승인과 구매자의 구매 확정
     */
    @Transactional
    public TransactionResponse updateTransactionStatus(String email, Long transactionId, TransactionStatusUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Transaction transaction = getTransactionOrThrow(transactionId);
        validateStatusUpdate(transaction, user, request.status());

        transaction.updateStatus(request.status());

        if (request.status() == TransactionStatus.COMPLETED) {
            handleTransactionCompletion(transaction);
        }

        return TransactionResponse.from(transaction);
    }

    /**
     * 거래 정보를 조회합니다.
     * 요구사항 6: 당사자간 거래내역 확인
     */
    public TransactionResponse getTransaction(Long userId, Long transactionId) {
        Transaction transaction = getTransactionOrThrow(transactionId);
        validateTransactionAccess(transaction, getUserOrThrow(userId));

        return TransactionResponse.from(transaction);
    }

    /**
     * 구매자의 거래 내역을 조회합니다.
     * 요구사항 7: 구매한 용품 목록 조회
     */
    public Page<TransactionResponse> getPurchasedTransactions(String email, Pageable pageable) {
        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return transactionRepository.findByBuyerAndStatus(
                buyer,
                TransactionStatus.COMPLETED,
                pageable
        ).map(TransactionResponse::from);
    }

    /**
     * 진행 중인 거래 내역을 조회합니다.
     * 요구사항 7: 예약중인 용품 목록 조회
     */
    public Page<TransactionResponse> getOngoingTransactions(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<TransactionStatus> ongoingStatuses = List.of(
                TransactionStatus.REQUESTED,
                TransactionStatus.APPROVED
        );

        return transactionRepository.findRecentTransactionsByUser(
                user,
                ongoingStatuses,
                pageable
        ).map(TransactionResponse::from);
    }

    /**
     * 상품의 거래 내역을 조회합니다.
     * 요구사항 6: 상품 상세정보의 거래내역 확인
     */
    public Page<TransactionResponse> getProductTransactions(Long userId, Long productId, Pageable pageable) {
        User user = getUserOrThrow(userId);
        Product product = getProductOrThrow(productId);

        validateProductAccess(product, user);

        return transactionRepository.findByProduct(product, pageable)
                .map(TransactionResponse::from);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private Transaction getTransactionOrThrow(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));
    }

    private void validateTransactionCreation(User buyer, Product product) {
        // 요구사항 1: 회원만 거래 가능
        if (buyer == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 자신의 상품은 구매할 수 없음
        if (buyer.getId().equals(product.getSeller().getId())) {
            throw new CustomException(ErrorCode.INVALID_TRANSACTION_PARTICIPANT);
        }

        // 요구사항 10: 동일 상품 중복 구매 불가
        if (transactionRepository.existsByProductAndBuyer(product, buyer)) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }

        // 상품 구매 가능 여부 확인
        if (!product.canPurchase()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_AVAILABLE);
        }
    }

    private void validateStatusUpdate(Transaction transaction, User user, TransactionStatus newStatus) {
        if (newStatus == null) {
            throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
        }

        TransactionStatus currentStatus = transaction.getStatus();

        switch (newStatus) {
            // 요구사항 8: 판매자의 판매 승인
            case APPROVED -> {
                if (!transaction.isSellerMatch(user) || currentStatus != TransactionStatus.REQUESTED) {
                    throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
                }
            }
            // 요구사항 11: 구매자의 구매 확정
            case CONFIRMED -> {
                if (!transaction.isBuyerMatch(user) || currentStatus != TransactionStatus.APPROVED) {
                    throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
                }
            }
            // 최종 완료 처리
            case COMPLETED -> {
                if (!transaction.isSellerMatch(user) || currentStatus != TransactionStatus.CONFIRMED) {
                    throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
                }
            }
            default -> throw new CustomException(ErrorCode.INVALID_STATUS_UPDATE);
        }
    }

    private void validateTransactionAccess(Transaction transaction, User user) {
        if (!transaction.isBuyerMatch(user) && !transaction.isSellerMatch(user)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_TRANSACTION_ACCESS);
        }
    }

    private void validateProductAccess(Product product, User user) {
        if (!product.isSellerMatch(user)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    private void handleTransactionCompletion(Transaction transaction) {
        Product product = transaction.getProduct();
        updateProductStatus(product);
    }

    /**
     * 요구사항 12: 수량에 따른 제품 상태 업데이트
     */
    private void updateProductStatus(Product product) {
        if (product.getQuantity() > 0) {
            product.updateStatus(ProductStatus.ON_SALE);
        } else {
            List<TransactionStatus> ongoingStatuses = List.of(
                    TransactionStatus.REQUESTED,
                    TransactionStatus.APPROVED,
                    TransactionStatus.CONFIRMED
            );

            boolean hasOngoingTransactions = transactionRepository.existsByProductAndStatusIn(
                    product,
                    ongoingStatuses
            );

            if (hasOngoingTransactions) {
                product.updateStatus(ProductStatus.RESERVED);
            } else {
                product.updateStatus(ProductStatus.COMPLETED);
            }
        }
    }
}
