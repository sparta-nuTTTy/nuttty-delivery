package com.nuttty.eureka.auth.presentation.controller;

import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.application.dto.UserSearchResponseDto;
import com.nuttty.eureka.auth.application.service.AuthService;
import com.nuttty.eureka.auth.application.service.UserService;
import com.nuttty.eureka.auth.presentation.request.UserRoleUpdateRequestDto;
import com.nuttty.eureka.auth.presentation.request.UserSearchRequestDto;
import com.nuttty.eureka.auth.util.ResultResponse;
import com.nuttty.eureka.auth.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    // 회원 상세 조회
    @GetMapping("/{user_id}")
    public ResultResponse<UserInfoDto> getUserInfo(@RequestHeader("X-User-Role") String role,
                                                   @RequestHeader("X-User-Id") Long loggedUserId,
                                                   @PathVariable("user_id") Long targetUserId) {

        return ResultResponse.<UserInfoDto>builder()
                .data(userService.getUserInfo(role, loggedUserId, targetUserId))
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }

    // 회원 권한 수정
    @PatchMapping("/{user_id}")
    public ResultResponse<UserInfoDto> updateUserRole(@RequestHeader("X-User-Role") String role,
                                                      @PathVariable("user_id") Long targetUserId,
                                                      @RequestBody UserRoleUpdateRequestDto updateRequestDto) {

        return ResultResponse.<UserInfoDto>builder()
                .data(userService.updateUserRole(role, targetUserId, updateRequestDto))
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
    }

    // 회원 탈퇴
    @DeleteMapping("/{user_id}")
    public ResultResponse<String> deleteUserInfo(@RequestHeader("X-User-Role") String role,
                                                 @PathVariable("user_id") Long targetUserId) {
        return ResultResponse.<String>builder()
                .data(userService.deleteUserInfo(role, targetUserId))
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();
    }

    // 회원 검색
    @GetMapping("/search")
    public ResultResponse<Page<UserSearchResponseDto>> searchUserInfo(@RequestHeader("X-User-Role") String role,
                                                                      @PageableDefault Pageable pageable,
                                                                      UserSearchRequestDto searchRequestDto) {

        Page<UserSearchResponseDto> searchUserList = userService.searchUserInfo(role, pageable, searchRequestDto);

        return ResultResponse.<Page<UserSearchResponseDto>>builder()
                .data(searchUserList)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }
}
