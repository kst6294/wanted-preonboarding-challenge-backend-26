package wanted.market.member.domain.dto.response.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordServiceDto {
    private Long userId;
    private boolean resetPasswordContinue;
    private Integer validationNumber;
}
