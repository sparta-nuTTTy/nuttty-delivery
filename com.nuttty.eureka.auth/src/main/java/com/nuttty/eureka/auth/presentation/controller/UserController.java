package com.nuttty.eureka.auth.presentation.controller;

import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.application.dto.UserSearchResponseDto;
import com.nuttty.eureka.auth.application.security.UserDetailsImpl;
import com.nuttty.eureka.auth.application.service.UserService;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.presentation.request.UserRoleUpdateRequestDto;
import com.nuttty.eureka.auth.presentation.request.UserSearchRequestDto;
import com.nuttty.eureka.auth.util.ResultResponse;
import com.nuttty.eureka.auth.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 상세 조회
    @GetMapping("/{user_id}")
    public ResultResponse<UserInfoDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @PathVariable("user_id") Long targetUserId) {

        UserRoleEnum loggedUserRole = userDetails.getUser().getRole();
        Long loggedUserId = userDetails.getUserId();

        // 마스터 관리자 이외에 유저는 자신의 정보만 조회 가능
        if (!loggedUserRole.equals(UserRoleEnum.MASTER) && !loggedUserId.equals(targetUserId)) {
            throw new AccessDeniedException("회원님의 정보가 아닙니다.");
        }

        return ResultResponse.<UserInfoDto>builder()
                .data(userService.getUserInfo(loggedUserRole, targetUserId))
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }

    // 회원 권한 수정
    @PatchMapping("/{user_id}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResultResponse<UserInfoDto> updateUserRole(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable("user_id") Long targetUserId,
                                                      @RequestBody UserRoleUpdateRequestDto updateRequestDto) {

        UserRoleEnum loggedUserRole = userDetails.getUser().getRole();

        return ResultResponse.<UserInfoDto>builder()
                .data(userService.updateUserRole(loggedUserRole, targetUserId, updateRequestDto))
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
    }

    // 회원 탈퇴
    @DeleteMapping("/{user_id}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResultResponse<String> deleteUserInfo(@PathVariable("user_id") Long targetUserId) {

        return ResultResponse.<String>builder()
                .data(userService.deleteUserInfo(targetUserId))
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();
    }

    // 회원 검색
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResultResponse<Page<UserSearchResponseDto>> searchUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @PageableDefault Pageable pageable,
                                                                      UserSearchRequestDto searchRequestDto) {
        UserRoleEnum loggedUserRole = userDetails.getUser().getRole();

        Page<UserSearchResponseDto> searchUserList = userService.searchUserInfo(loggedUserRole, pageable, searchRequestDto);

        return ResultResponse.<Page<UserSearchResponseDto>>builder()
                .data(searchUserList)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }
}
