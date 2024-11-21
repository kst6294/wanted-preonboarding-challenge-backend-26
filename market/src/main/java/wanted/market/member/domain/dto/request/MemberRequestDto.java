package wanted.market.member.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberRequestDto {
    @NotBlank
    private String login_id;
    @NotBlank
    private String password;
    @NotBlank
    private String username;
}
