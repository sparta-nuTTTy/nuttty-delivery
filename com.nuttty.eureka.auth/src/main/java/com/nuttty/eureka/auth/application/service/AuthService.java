package com.nuttty.eureka.auth.application.service;

import com.nuttty.eureka.auth.application.dto.TokenDto;
import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.exception.custom.InvalidAdminPasswordException;
import com.nuttty.eureka.auth.infrastructure.repository.UserRepository;
import com.nuttty.eureka.auth.presentation.request.LoginRequestDto;
import com.nuttty.eureka.auth.presentation.request.SignupRequestDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey secretKey;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${jwt.admin-token}")
    private String ADMIN_TOKEN;

    @Value(("${jwt.access-expiration}"))
    private Long accessExpiration;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, @Value("${jwt.secret-key}") String secretKey,
                       RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.redisTemplate = redisTemplate;
    }

    // 회원 가입
    @Transactional
    public UserInfoDto signup(SignupRequestDto signupRequestDto) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new DataIntegrityViolationException("사용중인 이메일 입니다.");
        }

        // 관리자 검증
        UserRoleEnum userRole = UserRoleEnum.valueOf(signupRequestDto.getRole());
        if (userRole.equals(UserRoleEnum.MASTER)) {
            if (!signupRequestDto.getAdmin_token().equals(ADMIN_TOKEN)) {
                throw new InvalidAdminPasswordException("관리자 암호가 올바르지 않습니다.");
            }
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        // 유저 객체 생성 & 저장
        User user = User.create(signupRequestDto.getUsername(), signupRequestDto.getEmail(), encodedPassword, userRole);
        userRepository.save(user);

        return UserInfoDto.of(user);
    }

    // 로그인
    @Transactional
    public TokenDto login(LoginRequestDto loginRequestDto) {
        // 가입 여부 확인
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 생성
        return createAccessToken(loginRequestDto.getEmail());
    }


    // 토큰 생성
    private TokenDto createAccessToken(String email) {
        // 이메일로 가입한 유저 존재 여부 검사
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));

        // 토큰 반환
        return userRepository.findByEmail(user.getEmail())
                // user_id & role 로 JWT 토큰을 생성
                .map(userInfo -> TokenDto.of(Jwts.builder()
                                .claim("user_id", userInfo.getUserId())
                                .claim("role", userInfo.getRole())
                                .claim("email", userInfo.getEmail())
                                .issuer(issuer)
                                .issuedAt(new Date(System.currentTimeMillis()))
                                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                                .signWith(secretKey)
                                .compact())
                        //유저가 존재하지 않는다면 Exception 을 발생 시킵니다.
                ).orElseThrow(() -> new EntityNotFoundException("Reject createAccessToken: 존재하지 않는 유저입니다."));
    }


    // userId 존재여부 검증 API
    @Cacheable(cacheNames = "userInfoCache", key = "#userId")
    public UserInfoDto verifyUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserInfoDto::of) // User가 존재하면 UserInfoDto로 변환
                .orElse(null); // 없으면 null 반환
    }
}
