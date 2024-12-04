package wanted.market.global.domain;

import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity {
    private LocalDateTime createDateTime;
    private LocalDateTime lastModifiedDateTime;

    public void setCreateDateTime() {
        this.createDateTime = LocalDateTime.now();
    }

    public void setLastModifiedDateTime() {
        this.lastModifiedDateTime = LocalDateTime.now();
    }
}
