package com.nuttty.eureka.hub.application.jwt;

import com.nuttty.eureka.hub.application.security.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "JwtUtil")
public class JwtUtil {

    private final UserDetailsServiceImpl userDetailsService;


    public JwtUtil(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // 인증 처리
    public void setAuthentication(String userId) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        log.info("토큰 생성 시작 #####");
        // 사용자 정보를 기반으로 인증 토큰 생성
        Authentication authentication = createAuthentication(userId);
        log.info("토큰 생성 완료 #####");
        log.info("SecurityContext 인증 정보 설정 시작 #####");
        // SecurityContext에 인증 정보 설정
        context.setAuthentication(authentication);
        log.info("SecurityContext 인증 정보 설정 완료 #####");

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    public Authentication createAuthentication(String userId) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}