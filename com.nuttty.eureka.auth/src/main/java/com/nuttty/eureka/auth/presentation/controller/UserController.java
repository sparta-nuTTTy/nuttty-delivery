package com.nuttty.eureka.auth.presentation.controller;

import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.application.dto.UserSearchResponseDto;
import com.nuttty.eureka.auth.application.service.UserService;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.presentation.request.UserRoleUpdateRequestDto;
import com.nuttty.eureka.auth.presentation.request.UserSearchRequestDto;
import com.nuttty.eureka.auth.util.ResultResponse;
import com.nuttty.eureka.auth.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 상세 조회
    @GetMapping("/{user_id}")
    public ResultResponse<UserInfoDto> getUserInfo(@RequestHeader("X-User-Role") String role,
                                                   @RequestHeader("X-User-Id") Long loggedUserId,
                                                   @PathVariable("user_id") Long targetUserId) {

        // 로그인 유저의 정보와 조회 할 유저의 정보 일치 검사 & 로그인 유저 권한 체크
        if (!loggedUserId.equals(targetUserId) && !UserRoleEnum.valueOf(role).equals(UserRoleEnum.MASTER)) {
            throw new AccessDeniedException("해당 유저의 정보를 조회 할 권한이 없습니다.");
        }

        return ResultResponse.<UserInfoDto>builder()
                .data(userService.getUserInfo(targetUserId))
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }

    // 회원 권한 수정
    @PatchMapping("/{user_id}")
    public ResultResponse<UserInfoDto> updateUserRole(@RequestHeader("X-User-Role") String role,
                                                      @PathVariable("user_id") Long targetUserId,
                                                      @RequestBody UserRoleUpdateRequestDto updateRequestDto) {
        validateUserRole(role);

        return ResultResponse.<UserInfoDto>builder()
                .data(userService.updateUserRole(targetUserId, updateRequestDto))
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
    }

    // 회원 탈퇴
    @DeleteMapping("/{user_id}")
    public ResultResponse<String> deleteUserInfo(@RequestHeader("X-User-Role") String role,
                                                 @PathVariable("user_id") Long targetUserId) {
        validateUserRole(role);

        return ResultResponse.<String>builder()
                .data(userService.deleteUserInfo(targetUserId))
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();
    }

    // 회원 검색
    @GetMapping("/search")
    public ResultResponse<Page<UserSearchResponseDto>> searchUserInfo(@RequestHeader("X-User-Role") String role,
                                                                      @PageableDefault Pageable pageable,
                                                                      UserSearchRequestDto searchRequestDto) {
        validateUserRole(role);

        Page<UserSearchResponseDto> searchUserList = userService.searchUserInfo(pageable, searchRequestDto);

        return ResultResponse.<Page<UserSearchResponseDto>>builder()
                .data(searchUserList)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }


    // 로그인 유저 권한 체크
    private void validateUserRole(String role) {

        if (!UserRoleEnum.valueOf(role).equals(UserRoleEnum.MASTER)) {
            throw new AccessDeniedException("접근 가능한 권한이 아닙니다.");
        }
    }
}
