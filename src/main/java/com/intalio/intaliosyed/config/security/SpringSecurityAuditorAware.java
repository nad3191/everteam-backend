package com.intalio.intaliosyed.config.security;

import com.intalio.intaliosyed.util.SecurityUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    private final SecurityUtil securityUtil;

    public SpringSecurityAuditorAware(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    public Optional<Long> getCurrentAuditor() {
        Optional<UserDetailsImpl> optional = securityUtil.userDetails();
        return optional.map(UserDetailsImpl::getId);
    }
}
