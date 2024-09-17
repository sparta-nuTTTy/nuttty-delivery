package com.nuttty.eureka.auth.application.jwt;

import com.nuttty.eureka.auth.application.dto.TokenDto;
import com.nuttty.eureka.auth.application.security.UserDetailsServiceImpl;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j(topic = "JwtUtil")
public class JwtUtil {

    private final UserDetailsServiceImpl userDetailsService;
    private final SecretKey secretKey;
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${spring.application.name}")
    private String issuer;

    @Value(("${jwt.access-expiration}"))
    private Long accessExpiration;


    public JwtUtil(UserDetailsServiceImpl userDetailsService, @Value("${jwt.secret-key}") String secretKey) {
        this.userDetailsService = userDetailsService;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    // 토큰 생성
    public TokenDto createAccessToken(Long userId, UserRoleEnum userRole) {
        // user_id & role 로 JWT 토큰을 생성
        log.info("createAccessToken start");
        return TokenDto.of(BEARER_PREFIX + Jwts.builder()
                .subject(userId.toString())
                .claim("role", userRole)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey)
                .compact());
    }

    // 인증 처리
    public void setAuthentication(String userId) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        log.info("토큰 생성 시작 #####");
        // 사용자 정보를 기반으로 인증 토큰 생성
        Authentication authentication = createAuthentication(userId, null);
        log.info("토큰 생성 완료 #####");
        log.info("SecurityContext 인증 정보 설정 시작 #####");
        // SecurityContext에 인증 정보 설정
        context.setAuthentication(authentication);
        log.info("SecurityContext 인증 정보 설정 완료 #####");

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    public Authentication createAuthentication(String userId, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
}
