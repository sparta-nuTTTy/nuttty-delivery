package com.nuttty.eureka.order.config;

import com.nuttty.eureka.order.application.jwt.CustomPreAuthFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomPreAuthFilter customPreAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()) // 모든 요청을 허용
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(new CustomIPFilter(), UsernamePasswordAuthenticationFilter.class); // IP 필터 추가
        http.addFilterBefore(customPreAuthFilter, UsernamePasswordAuthenticationFilter.class); // CustomPreFilter 인가 과정 추가
        return http.build();
    }
}

@Slf4j
class CustomIPFilter extends OncePerRequestFilter {
    private static final String GATEWAY_PORT = "19092";
    private static final String LOCAL_IPv4 = "127.0.0.1";
    private static final String LOCAL_IPv6 = "0:0:0:0:0:0:0:1";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        System.out.println(requestURI);

        if (requestURI.startsWith("/api-docs") ||
                requestURI.startsWith("/swagger-ui-custom.html") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-ui")) {
            chain.doFilter(request, response);
            return;// 해당 URL은 통과
        }

        String remoteAddr = request.getRemoteAddr();
        String forwardedPort = request.getHeader("X-Forwarded-Port");

        if ((LOCAL_IPv4.equals(remoteAddr) || LOCAL_IPv6.equals(remoteAddr)) && GATEWAY_PORT.equals(forwardedPort)) {
            chain.doFilter(request, response);
        } else {
            log.error("IP OR PORT ERROR | IP: {}, PORT: {}", remoteAddr, forwardedPort);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}