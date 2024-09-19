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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "회원", description = "회원 상세 조회, 권한 수정, 탈퇴, 검색 API")
public class UserController {

    private final UserService userService;

    // 회원 상세 조회
    @GetMapping("/{user_id}")
    @Operation(summary = "회원 상세 정보 조회", description = "회원의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 상세 조회 성공")
    public ResultResponse<UserInfoDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @Schema(description = "회원 식별자", example = "Long", required = true)
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
    @Operation(summary = "회원 권한 수정", description = "회원의 권한을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "회원 권한 수정 성공")
    public ResultResponse<UserInfoDto> updateUserRole(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @Schema(description = "회원 식별자", example = "Long", required = true)
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
    @Operation(summary = "회원 탈퇴", description = "회원 정보를 논리적 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
    public ResultResponse<String> deleteUserInfo(@Schema(description = "회원 식별자", example = "Long", required = true)
                                                 @PathVariable("user_id") Long targetUserId) {

        return ResultResponse.<String>builder()
                .data(userService.deleteUserInfo(targetUserId))
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();
    }

    // 회원 검색
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('MASTER')")
    @Operation(summary = "회원 검색", description = "회원 리스트를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "회원 검색 성공")
    public ResultResponse<Page<UserSearchResponseDto>> searchUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @Schema(description = "page, size, sort", example = "int, int, String", required = true)
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
