package com.nuttty.eureka.company.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private final HttpServletRequest httpServletRequest;

    @Override
    public Optional<String> getCurrentAuditor() {
        String byWho = httpServletRequest.getHeader("X-User-Email");
        if (!StringUtils.hasText(byWho)) {
            byWho = "system";
        }
        return Optional.of(byWho);
    }
}