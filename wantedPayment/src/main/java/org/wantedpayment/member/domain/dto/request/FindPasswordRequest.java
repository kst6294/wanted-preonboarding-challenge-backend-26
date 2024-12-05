package org.wantedpayment.member.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindPasswordRequest {
    private String loginId;
    private String name;
    private String newPassword;
    private String confirmPassword;
}
