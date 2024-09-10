package com.nuttty.eureka.auth.presentation.controller;

import com.nuttty.eureka.auth.application.dto.TokenDto;
import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.application.service.AuthService;
import com.nuttty.eureka.auth.presentation.request.LoginRequestDto;
import com.nuttty.eureka.auth.presentation.request.SignupRequestDto;
import com.nuttty.eureka.auth.util.ResultResponse;
import com.nuttty.eureka.auth.util.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원 가입
    @PostMapping("/signup")
    public ResultResponse<UserInfoDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {

        return ResultResponse.<UserInfoDto>builder()
                .data(authService.signup(signupRequestDto))
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();
    }

    // 로그인
    @PostMapping("/login")
    public ResultResponse<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto) {

        return ResultResponse.<TokenDto>builder()
                .data(authService.login(loginRequestDto))
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();
    }

    // userId 존재여부 검증 API
    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyUser(final @RequestParam(value = "user_id") Long userId) {
        Boolean response = authService.verifyUser(userId);
        return ResponseEntity.ok(response);
    }
}
