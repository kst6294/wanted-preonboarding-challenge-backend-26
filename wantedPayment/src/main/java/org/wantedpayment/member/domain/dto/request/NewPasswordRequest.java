package org.wantedpayment.member.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewPasswordRequest {
    private String newPassword;
    private String confirmPassword;
}
