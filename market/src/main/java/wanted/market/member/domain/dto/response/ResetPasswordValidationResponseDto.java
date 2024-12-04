package wanted.market.member.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordValidationResponseDto {
    private Long userId;
    private boolean resetSuccess;
    private String uuid;
    private String message;
}
