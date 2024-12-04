package wanted.market.member.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordUserResponseDto {
    private boolean resetPasswordContinue;
    private String message;
}
