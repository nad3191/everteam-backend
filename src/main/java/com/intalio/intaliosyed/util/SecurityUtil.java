package com.intalio.intaliosyed.util;

import com.intalio.intaliosyed.config.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtil {

    public Optional<UserDetailsImpl> userDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.ofNullable((UserDetailsImpl) authentication.getPrincipal());
    }

    public Long getUserId() {
        return userDetails().orElseThrow(() -> new RuntimeException("Unauthorized")).getId();
    }

}
