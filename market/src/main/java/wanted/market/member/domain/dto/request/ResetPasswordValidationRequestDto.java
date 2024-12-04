package wanted.market.member.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordValidationRequestDto {
    private String userLoginId;
    private Integer validationNumber;
}
