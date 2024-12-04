package wanted.market.member.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordRequestDto {
    private String userId;
    private String userEmail;
}
