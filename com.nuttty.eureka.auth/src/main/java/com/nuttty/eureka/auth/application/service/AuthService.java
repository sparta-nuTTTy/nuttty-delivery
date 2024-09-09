package com.nuttty.eureka.auth.application.service;

import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.domain.repository.UserRepository;
import com.nuttty.eureka.auth.presentation.request.SignupRequestDto;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey secretKey;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${jwt.admin-token}")
    private String ADMIN_TOKEN;

    @Value(("${jwt.access-expiration}"))
    private Long accessExpiration;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, @Value("${jwt.secret-key}") String secretKey) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    // 회원 가입
    @Transactional
    public UserInfoDto signup(SignupRequestDto signupRequestDto) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new IllegalArgumentException("사용중인 이메일 입니다.");
        }
        // 관리자 검증
        UserRoleEnum userRole = UserRoleEnum.valueOf(signupRequestDto.getRole());
        if (userRole.equals(UserRoleEnum.MASTER)) {
            if (!signupRequestDto.getAdmin_token().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 올바르지 않습니다.");
            }
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        // 유저 객체 생성 & 저장
        User user = User.create(signupRequestDto.getUsername(), signupRequestDto.getEmail(), encodedPassword, userRole);
        userRepository.save(user);

        return new UserInfoDto(user);
    }
}
