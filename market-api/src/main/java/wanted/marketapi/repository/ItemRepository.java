package wanted.marketapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.marketapi.domain.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
