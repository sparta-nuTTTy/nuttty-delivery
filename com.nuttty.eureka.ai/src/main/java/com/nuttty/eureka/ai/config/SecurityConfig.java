package com.nuttty.eureka.ai.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new CustomIPFilter(), UsernamePasswordAuthenticationFilter.class) // IP 필터 추가
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // 모든 요청을 허용 또는 아래에서 직접 차단 가능
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

class CustomIPFilter extends UsernamePasswordAuthenticationFilter {
    private static final String GATEWAY_IP = "19092"; // 게이트웨이 IP 주소

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) {
        String remoteAddr = request.getRemoteAddr();

        // 로컬호스트와 게이트웨이 IP에서만 접근을 허용
        if ("127.0.0.1".equals(remoteAddr) || GATEWAY_IP.equals(remoteAddr)) {
            return false; // 허용된 IP
        } else {
            return true; // 차단된 IP
        }
    }
}
