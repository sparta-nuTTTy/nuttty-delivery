package com.nuttty.eureka.auth.presentation.controller;

import com.nuttty.eureka.auth.application.dto.TokenDto;
import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.application.service.AuthService;
import com.nuttty.eureka.auth.presentation.request.LoginRequestDto;
import com.nuttty.eureka.auth.presentation.request.SignupRequestDto;
import com.nuttty.eureka.auth.util.ResultResponse;
import com.nuttty.eureka.auth.util.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "회원 가입, 로그인, FeignClient 회원 정보 조회 API")
public class AuthController {

    private final AuthService authService;

    // 회원 가입
    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "회원을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원 가입 성공")
    public ResultResponse<UserInfoDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {

        return ResultResponse.<UserInfoDto>builder()
                .data(authService.signup(signupRequestDto))
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 합니다.")
    @ApiResponse(responseCode = "201", description = "로그인 성공")
    public ResultResponse<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto) {

        return ResultResponse.<TokenDto>builder()
                .data(authService.login(loginRequestDto))
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();
    }

    // FeignClient로 요청 될 유저 상세정보 메서드
    @GetMapping("/users/{user_id}/info")
    @Operation(summary = "FeignClient 유저 정보 조회", description = "서비스 내부 간 유저정보 조회")
    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공")
    public ResponseEntity<UserInfoDto> fetchUserInfo(@Schema(description = "회원 식별자", example = "Long", required = true)
                                                     @PathVariable("user_id") Long userId) {

        return ResponseEntity.ok(authService.fetchUserInfo(userId));
    }
}
