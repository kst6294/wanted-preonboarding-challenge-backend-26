package wanted.market.member.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewPasswordRequestDto {
    private String newPassword;
    private String uuid;
}
