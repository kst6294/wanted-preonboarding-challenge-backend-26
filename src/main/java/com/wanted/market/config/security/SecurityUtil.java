package com.wanted.market.config.security;

import com.wanted.market.common.exception.CustomException;
import com.wanted.market.common.exception.ErrorCode;
import com.wanted.market.domain.user.User;
import com.wanted.market.domain.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getName() == null) {
            throw new CustomException(ErrorCode.NOT_AUTHENTICATED);
        }
        
        return authentication.getName();
    }

    public static User getCurrentUser(UserRepository userRepository) {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
