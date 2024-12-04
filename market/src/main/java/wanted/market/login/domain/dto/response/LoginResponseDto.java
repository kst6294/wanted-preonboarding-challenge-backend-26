package wanted.market.login.domain.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private boolean loginSuccess;
    private String username;
}
