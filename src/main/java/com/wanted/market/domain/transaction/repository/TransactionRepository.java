package com.wanted.market.domain.transaction.repository;

import com.wanted.market.domain.product.Product;
import com.wanted.market.domain.transaction.Transaction;
import com.wanted.market.domain.transaction.TransactionStatus;
import com.wanted.market.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * 구매자의 거래 내역을 조회합니다.
     */
    Page<Transaction> findByBuyer(User buyer, Pageable pageable);

    /**
     * 판매자의 거래 내역을 조회합니다.
     */
    Page<Transaction> findBySeller(User seller, Pageable pageable);

    /**
     * 구매자와 거래 상태로 거래 내역을 조회합니다.
     * 7번 요구사항: "구매한 용품"과 "예약중인 용품" 목록 조회에 사용
     */
    Page<Transaction> findByBuyerAndStatus(User buyer, TransactionStatus status, Pageable pageable);

    /**
     * 판매자와 거래 상태로 거래 내역을 조회합니다.
     * 7번 요구사항: "예약중인 용품" 목록 조회에 사용
     */
    Page<Transaction> findBySellerAndStatus(User seller, TransactionStatus status, Pageable pageable);

    /**
     * 상품에 대한 거래 내역을 조회합니다.
     * 6번 요구사항: 상품 상세정보 조회 시 거래 내역 확인에 사용
     */
    Page<Transaction> findByProduct(Product product, Pageable pageable);

    /**
     * 특정 상품의 완료된 거래 개수를 조회합니다.
     * 12번 요구사항: 제품 상태 변경을 위한 완료된 거래 수 확인에 사용
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.product = :product AND t.status = :status")
    long countCompletedTransactionsByProduct(@Param("product") Product product, @Param("status") TransactionStatus status);

    /**
     * 거래 ID와 구매자로 거래를 조회합니다.
     * 11번 요구사항: 구매자의 구매확정 권한 검증에 사용
     */
    Optional<Transaction> findByIdAndBuyer(Long id, User buyer);

    /**
     * 거래 ID와 판매자로 거래를 조회합니다.
     * 8번 요구사항: 판매자의 판매승인 권한 검증에 사용
     */
    Optional<Transaction> findByIdAndSeller(Long id, User seller);

    /**
     * 특정 상품의 진행 중인 거래가 있는지 확인합니다.
     * 12번 요구사항: 제품 상태 변경을 위한 진행 중인 거래 확인에 사용
     */
    boolean existsByProductAndStatusIn(Product product, List<TransactionStatus> statuses);

    /**
     * 특정 사용자(구매자 또는 판매자)의 최근 거래 내역을 조회합니다.
     * 6번 요구사항: 당사자간 거래내역 확인에 사용
     */
    @Query("SELECT t FROM Transaction t WHERE (t.buyer = :user OR t.seller = :user) AND t.status IN :statuses ORDER BY t.createdAt DESC")
    Page<Transaction> findRecentTransactionsByUser(@Param("user") User user, @Param("statuses") List<TransactionStatus> statuses, Pageable pageable);

    /**
     * 특정 상품에 대한 사용자의 거래 여부를 확인합니다.
     * 10번 요구사항: 한 명당 1개 구매 제한 검증에 사용
     */
    boolean existsByProductAndBuyer(Product product, User buyer);
}
