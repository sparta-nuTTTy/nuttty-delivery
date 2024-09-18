package com.nuttty.eureka.auth.application.service;

import com.nuttty.eureka.auth.application.dto.TokenDto;
import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.exception.custom.InvalidAdminPasswordException;
import com.nuttty.eureka.auth.application.jwt.JwtUtil;
import com.nuttty.eureka.auth.infrastructure.repository.UserRepository;
import com.nuttty.eureka.auth.presentation.request.LoginRequestDto;
import com.nuttty.eureka.auth.presentation.request.SignupRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "AuthService")
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.admin-token}")
    private String ADMIN_TOKEN;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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
        log.info("가입 여부 확인 완료 #####");

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        log.info("비밀번호 일치여부 확인 완료 #####");

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // Security 인증 검사
        Authentication authentication = jwtUtil.createAuthentication(user.getUserId().toString(), loginRequestDto.getPassword());
        context.setAuthentication(authentication);
        log.info("컨텍스트 홀더 저장 완료 #####");

        // 토큰 생성
        log.info("토큰 생성 시작 #####");
        return jwtUtil.createAccessToken(user.getUserId(), user.getRole());
    }

    // Other-Service 에서 User의 정보를 호출하기 위한 메서드
    @Transactional(readOnly = true)
    public UserInfoDto fetchUserInfo(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));

        return UserInfoDto.of(user);
    }
}
