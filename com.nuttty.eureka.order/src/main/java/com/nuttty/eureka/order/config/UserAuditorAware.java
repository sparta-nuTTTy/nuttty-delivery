package com.nuttty.eureka.order.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class UserAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // RequestContextHolder에 요청 정보가 있는지 확인
        if (RequestContextHolder.getRequestAttributes() != null) {
            // 현재 요청이 있을 때
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                            .getRequest();
            String byWho = request.getHeader("X-User-Email");
            if (!StringUtils.hasText(byWho)) {
                byWho = "system";
            }
            return Optional.of(byWho);
        } else {
            // 요청이 없는 상황에서는 "system" 반환
            return Optional.of("system");
        }
    }
}
