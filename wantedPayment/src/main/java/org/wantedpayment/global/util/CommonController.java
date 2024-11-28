package org.wantedpayment.global.util;

import jakarta.servlet.http.HttpServletRequest;

public class CommonController {
    public Long getLoginMemberId(HttpServletRequest httpServletRequest) {
        return (Long) httpServletRequest.getSession().getAttribute("memberId");
    }
}
