package com.wanted.market.domain.product.repository;

import com.wanted.market.domain.product.Product;
import com.wanted.market.domain.product.ProductStatus;
import com.wanted.market.domain.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 상품 상태로 상품 목록을 조회합니다.
     */
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    /**
     * 판매자가 등록한 상품 목록을 조회합니다.
     */
    Page<Product> findBySeller(User seller, Pageable pageable);

    /**
     * 판매자와 상품 상태로 상품 목록을 조회합니다.
     */
    Page<Product> findBySellerAndStatus(User seller, ProductStatus status, Pageable pageable);

    /**
     * 상품 ID와 판매자로 상품을 조회합니다.
     * 판매자 검증에 사용됩니다.
     */
    Optional<Product> findByIdAndSeller(Long id, User seller);

    /**
     * 상품을 pessimistic write lock으로 조회합니다.
     * 동시성 제어가 필요한 재고 관리에 사용됩니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithPessimisticLock(@Param("id") Long id);

    /**
     * 상품을 optimistic lock으로 조회합니다.
     * version을 통한 동시성 제어에 사용됩니다.
     */
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithOptimisticLock(@Param("id") Long id);

    /**
     * 상품 이름으로 상품을 검색합니다.
     * 부분 일치 검색이 가능합니다.
     */
    Page<Product> findByNameContaining(String name, Pageable pageable);

    /**
     * 판매 중이면서 재고가 있는 상품만 조회합니다.
     */
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.quantity > 0")
    Page<Product> findAvailableProducts(@Param("status") ProductStatus status, Pageable pageable);
}