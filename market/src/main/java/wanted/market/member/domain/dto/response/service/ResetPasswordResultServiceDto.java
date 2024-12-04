package wanted.market.member.domain.dto.response.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordResultServiceDto {
    private Long userId;
    private boolean resetPasswordSuccess;
}
