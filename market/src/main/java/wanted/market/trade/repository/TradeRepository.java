package wanted.market.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.market.trade.domain.entity.Trade;

import java.util.List;
import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByBuyerId(Long buyerId);

    List<Trade> findBySellerId(Long sellerId);

    Optional<Trade> findByMerchantUid(String merchantUid);
}
