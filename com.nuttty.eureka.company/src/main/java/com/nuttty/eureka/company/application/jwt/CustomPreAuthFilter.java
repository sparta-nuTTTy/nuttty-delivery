package com.nuttty.eureka.company.application.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j(topic = "Ai Authorization")
@RequiredArgsConstructor
public class CustomPreAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 인증(auth) 관련 요청은 토큰 X
        if (request.getRequestURI().startsWith("/api/v1/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("CustomPreAuthFilter 동작 시작 #####");

        String userId = request.getHeader("X-User-Id");
        String userRole = request.getHeader("X-User-Role");

        log.info("userID: {}", userId);
        log.info("userRole: {}", userRole);
        if (userId != null && userRole != null) {
            log.info("유저 아이디 & 권한 isPresent!!! #####");

            try {
                log.info("인증 설정 시작 #####");
                jwtUtil.setAuthentication(userId);
                log.info("인증 설정 성공 #####");
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        } else {
            log.error("유저 아이디 & 권한 isNull!!! #####");
            // 빈 권한 처리
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    null,
                    null,
                    AuthorityUtils.NO_AUTHORITIES
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        log.info("---------------------------구분선------------------------------");
        filterChain.doFilter(request, response);
    }
}