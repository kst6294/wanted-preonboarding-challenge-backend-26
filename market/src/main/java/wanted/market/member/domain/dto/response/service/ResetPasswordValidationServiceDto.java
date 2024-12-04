package wanted.market.member.domain.dto.response.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordValidationServiceDto {
    private Long userId;
    private boolean resetSuccess;
    private String message;
}
