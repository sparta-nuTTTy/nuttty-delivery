package com.nuttty.eureka.company.config;

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
                        .anyRequest().permitAll() // 모든 요청을 허용
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

class CustomIPFilter extends UsernamePasswordAuthenticationFilter {
    private static final String GATEWAY_PORT = "19092";
    private static final String LOCAL_IPv4 = "127.0.0.1";
    private static final String LOCAL_IPv6 = "0:0:0:0:0:0:0:1";

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) {
        String remoteAddr = request.getRemoteAddr();
        String forwardedPort = request.getHeader("X-Forwarded-Port");

        // 로컬호스트와 게이트웨이 IP에서만 접근을 허용
        if ((LOCAL_IPv4.equals(remoteAddr) || LOCAL_IPv6.equals(remoteAddr)) && GATEWAY_PORT.equals(forwardedPort)) {
            return false; // 허용된 IP
        } else {
            return true; // 차단된 IP
        }
    }
}
