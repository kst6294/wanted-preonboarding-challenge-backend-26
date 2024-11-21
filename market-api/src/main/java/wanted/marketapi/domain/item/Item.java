package wanted.marketapi.domain.item;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Item {
    @Id
    @GeneratedValue
    private Long item_id;

    private String itemName;

    private int price;

    private State state;

    public Item() {
    }

    public Item(String itemName, int price, State state) {
        this.itemName = itemName;
        this.price = price;
        this.state = state;
    }
}
