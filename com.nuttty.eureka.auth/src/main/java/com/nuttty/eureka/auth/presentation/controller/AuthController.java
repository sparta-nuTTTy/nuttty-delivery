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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto) {

        TokenDto accessToken = authService.login(loginRequestDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, accessToken.toString())
                .body(accessToken);
    }
}
