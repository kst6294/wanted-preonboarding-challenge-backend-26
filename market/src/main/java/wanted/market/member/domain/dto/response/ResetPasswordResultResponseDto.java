package wanted.market.member.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordResultResponseDto {
    private Long userId;
    private boolean resetPasswordSuccess;
    private String message;
}
