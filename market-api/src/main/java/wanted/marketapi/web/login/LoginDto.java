package wanted.marketapi.web.login;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginDto {
    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;
}
