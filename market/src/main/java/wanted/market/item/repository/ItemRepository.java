package wanted.market.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.market.item.domain.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
