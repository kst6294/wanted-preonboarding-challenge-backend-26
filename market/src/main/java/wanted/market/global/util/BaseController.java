package wanted.market.global.util;

import jakarta.servlet.http.HttpServletRequest;
import wanted.market.login.session.SessionConst;

import static wanted.market.login.session.SessionConst.*;

public class BaseController {
    public static Long getMemberIdFromSession(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute(LOGIN_MEMBER);
    }
}
