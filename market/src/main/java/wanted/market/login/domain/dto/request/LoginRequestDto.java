package wanted.market.login.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;
}
