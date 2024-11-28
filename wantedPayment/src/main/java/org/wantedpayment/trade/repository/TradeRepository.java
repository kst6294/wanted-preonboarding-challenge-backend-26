package org.wantedpayment.trade.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wantedpayment.trade.domain.entity.Trade;

import java.util.List;
import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByItemId(Long id);
    Optional<Trade> findByItemIdAndBuyerId(Long id, Long buyerId);
    Page<Trade> findByBuyerId(Pageable pageable, Long memberId);
    Page<Trade> findBySellerId(Pageable pageable, Long memberId);
}
