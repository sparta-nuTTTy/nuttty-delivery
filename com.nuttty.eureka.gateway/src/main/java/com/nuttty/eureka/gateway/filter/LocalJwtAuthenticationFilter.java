package com.nuttty.eureka.gateway.filter;

import com.nuttty.eureka.gateway.application.dto.CustomHeaderDto;
import com.nuttty.eureka.gateway.application.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
@Slf4j(topic = "JwtAuthenticationFilter")
public class LocalJwtAuthenticationFilter implements GlobalFilter {

    private final String secretKey;
    private final AuthService authService;
    static final String BEARER_PREFIX = "Bearer ";

    // FeignClient 와 Global Filter 의 순환참조 문제 발생 >> Bean 초기 로딩 시 순환을 막기 위해 @Lazy 어노테이션을 추가
    public LocalJwtAuthenticationFilter(@Value("${jwt.secret-key}") String secretKey, @Lazy AuthService authService) {
        this.secretKey = secretKey;
        this.authService = authService;
    }

    // 외부 요청 보호 GlobalFilter - Security Filter 로 수정 필요
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        // 접근하는 URI 의 Path 값을 받아옵니다.
        String path = exchange.getRequest().getURI().getPath();
        // /auth 로 시작하는 요청들은 검증하지 않습니다.
        if (path.startsWith("/api/v1/auth")) {
            return chain.filter(exchange);
        }

        log.info("[filter]: auth 이외의 요청");
        // 토큰
        final String token = extractToken(exchange);
        // 토큰이 존재하지 않거나, validateToken(token) 기준에 부합하지 않으면 401 에러를 응답
        if (token == null || !validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        log.info("[filter]: 토큰 추출 성공");
        final Long userId = getUserId(token); // 유저 아이디
        final String role = getRoleOrEmail(token, "role"); // 유저 권한
        final String email = getRoleOrEmail(token, "email");

        // 헤더에 사용자 정보 담기
        ServerWebExchange modifiedExchange = setCustomHeader(exchange,
                CustomHeaderDto.builder()
                        .token(token)
                        .userId(userId)
                        .role(role)
                        .email(email)
                        .build());
        log.info("[filter]: 헤더에 사용자 정보 담기 성공");

        return chain.filter(modifiedExchange);
    }


    // 토큰 추출
    private String extractToken(ServerWebExchange exchange) {
        log.info("[extractToken]: 추출 시작");
        // Request Header 에서 Authorization 불러오기
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }


    // 토큰 유효성 검사
    private boolean validateToken(String token) {
        try {
            log.info("[validateToken]: 토큰 유효성 검사 시작");
            // String -> SecretKey 변환
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
            // JWT 에 설정된 정보를 불러오기
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build().parseSignedClaims(token);
            log.info("[validateToken]: jwt info payload");
            // JWT 값 중 Payload 부분에 user_id 로 설정된 값이 있는 경우
            if (claimsJws.getPayload().get("user_id") != null) {
                Long userId = ((Number) claimsJws.getPayload().get("user_id")).longValue();
                log.info("[validateToken]: userId: {}", userId);
                // user_id 값으로 해당 유저가 회원가입 한 유저인지 인증 서비스를 통해 확인
                return authService.verifyUser(userId);
            } else {
                log.error("[validateToken]: user_id is null");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    // 사용자 정보를 헤더에 담기
    public ServerWebExchange setCustomHeader(ServerWebExchange exchange, CustomHeaderDto responseDto) {
        return exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + responseDto.getToken())
                        .header("X-User-Id", responseDto.getUserId().toString())
                        .header("X-User-Role", responseDto.getRole())
                        .header("X-User-Email", responseDto.getEmail())
                        .build())
                .build();
    }


    private Long getUserId(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));

        return ((Number) Jwts.parser()
                .verifyWith(key)
                .build().parseSignedClaims(token)
                .getPayload().get("user_id")).longValue();
    }

    private String getRoleOrEmail(String token, String keyword) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));

        return Jwts.parser()
                .verifyWith(key)
                .build().parseSignedClaims(token)
                .getPayload().get(keyword).toString();
    }
}