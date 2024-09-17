package com.nuttty.eureka.gateway.configuration;

import com.nuttty.eureka.gateway.application.dto.CustomHeaderDto;
import com.nuttty.eureka.gateway.application.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF 비활성화
                // gateway 서비스 jwtAuthenticationFilter 검증 >> 토큰 검증 단계
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    @Bean
    public WebFilter jwtAuthenticationFilter() {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();
            if (path.startsWith("/api/v1/auth")) { // auth 관련 요청은 토큰 x >> 검증 x
                log.info("/api/v1/auth 요청 #####");
                return chain.filter(exchange);
            }

            log.info("토큰 필요 요청 #####");
            // 토큰 추출
            final String token = jwtUtil.extractToken(exchange.getRequest());
            log.info("토큰 추출 : {}", token);

            // JWT 유효성 검사
            if (!jwtUtil.validateToken(token)) {
                // 토큰 존재 x 거나 유효하지 않으면 401 에러x
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                log.error("토큰 유효성 검사 실패 #####");
                return exchange.getResponse().setComplete();
            }

            log.info("토큰 유효성 검사 성공 #####");

            // 토큰 정보 가져오기
            Claims claims = Jwts.parser()
                    .verifyWith(jwtUtil.secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.getSubject();
            String role = claims.get("role").toString();

            // 헤더에 사용자 정보 추가
            ServerWebExchange modifiedExchange = jwtUtil.setCustomHeader(exchange,
                    CustomHeaderDto.builder()
                            .token(token)
                            .userId(userId)
                            .role(role)
                            .build());

            log.info("---------------------------구분선------------------------------");
            // 요청을 수정하여 사용자 정보를 추가
            return chain.filter(modifiedExchange);
        };
    }
}
