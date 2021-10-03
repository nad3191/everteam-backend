package com.intalio.intaliosyed;

import com.intalio.intaliosyed.config.security.SpringSecurityAuditorAware;
import com.intalio.intaliosyed.util.SecurityUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
public class IntalioApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntalioApplication.class, args);
    }

    @Bean
    public AuditorAware<Long> auditorProvider(SecurityUtil securityUtil) {
        return new SpringSecurityAuditorAware(securityUtil);
    }
}
