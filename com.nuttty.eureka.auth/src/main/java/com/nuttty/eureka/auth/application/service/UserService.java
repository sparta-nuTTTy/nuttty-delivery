package com.nuttty.eureka.auth.application.service;

import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.application.dto.UserSearchResponseDto;
import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.infrastructure.repository.UserRepository;
import com.nuttty.eureka.auth.presentation.request.UserRoleUpdateRequestDto;
import com.nuttty.eureka.auth.presentation.request.UserSearchRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원 상세 조회
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userInfoCache", key = "#targetUserId")
    public UserInfoDto getUserInfo(String role, Long loggedUserId, Long targetUserId) {
        // 조회 유저 가입 여부 검사
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));

        // 로그인 유저의 정보와 조회 할 유저의 정보 일치 검사 & 로그인 유저 권한 체크
        if (!loggedUserId.equals(targetUserId) && !UserRoleEnum.valueOf(role).equals(UserRoleEnum.MASTER)) {
            throw new AccessDeniedException("해당 유저의 정보를 조회 할 권한이 없습니다.");
        }

        return UserInfoDto.of(targetUser);
    }


    // 회원 권한 수정 - 토큰 재발급
    @Transactional
    @CachePut(cacheNames = "userInfoCache", key = "#targetUserId")
    @CacheEvict(cacheNames = "{userInfoCache, userSearchInfoCache}", allEntries = true)
    public UserInfoDto updateUserRole(String role, Long targetUserId, UserRoleUpdateRequestDto updateRequestDto) {
        // 조회 유저 가입 여부 검사
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));

        // 로그인 유저 권한 체크
        if (!UserRoleEnum.valueOf(role).equals(UserRoleEnum.MASTER)) {
            throw new AccessDeniedException("해당 유저의 정보를 수정 할 권한이 없습니다.");
        }

        // 회원 권한 수정
        user.updateUserRole(UserRoleEnum.valueOf(updateRequestDto.getRole()));

        return UserInfoDto.of(user);
    }

    // 회원 탈퇴
    @Transactional
    @CacheEvict(cacheNames = "{userInfoCache, userSearchInfoCache}", allEntries = true)
    public String deleteUserInfo(String role, Long targetUserId) {
        // 조회 유저 가입 여부 검사
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));

        // 로그인 유저 권한 체크
        if (!UserRoleEnum.valueOf(role).equals(UserRoleEnum.MASTER)) {
            throw new AccessDeniedException("해당 유저의 정보를 삭제 할 권한이 없습니다.");
        }

        // 논리적 회원 삭제
        user.delete(user.getEmail());

        return "[" + user.getUsername() + "] 회원님 탈퇴 완료.";
    }


    // 회원 검색
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userSearchInfoCache", key = "{#searchRequestDto.user_id, #searchRequestDto.role, #searchRequestDto.username, #searchRequestDto.email}")
    public Page<UserSearchResponseDto> searchUserInfo(String role, Pageable pageable, UserSearchRequestDto searchRequestDto) {
        // 로그인 유저 권한 체크
        if (!UserRoleEnum.valueOf(role).equals(UserRoleEnum.MASTER)) {
            throw new AccessDeniedException("유저 정보를 검색 할 권한이 없습니다.");
        }

        return userRepository.findAllUser(pageable, searchRequestDto);
    }
}
