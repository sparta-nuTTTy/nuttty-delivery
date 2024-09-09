package com.nuttty.eureka.auth.presentation.controller;

import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.application.service.UserService;
import com.nuttty.eureka.auth.util.ResultResponse;
import com.nuttty.eureka.auth.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 상세 조회
    @GetMapping("/{user_id}")
    public ResultResponse<UserInfoDto> getUserInfo(@RequestHeader("X-User-Id") Long loggedUserId,
                                                   @PathVariable("user_id") Long targetUserId) {

        return ResultResponse.<UserInfoDto>builder()
                .data(userService.getUserInfo(loggedUserId, targetUserId))
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }
}
