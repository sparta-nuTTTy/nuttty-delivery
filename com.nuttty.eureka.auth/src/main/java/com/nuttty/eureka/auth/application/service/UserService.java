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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원 상세 조회
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userInfoCache", key = "#userId")
    public UserInfoDto getUserInfo(Long userId) {
        // 조회 유저 가입 여부 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));

        return UserInfoDto.of(user);
    }


    // 회원 권한 수정 - 토큰 재발급
    @Transactional
    @CachePut(cacheNames = "userInfoCache", key = "#userId")
    @CacheEvict(cacheNames = {"userInfoCache", "userSearchInfoCache"}, allEntries = true)
    public UserInfoDto updateUserRole(Long userId, UserRoleUpdateRequestDto updateRequestDto) {
        // 조회 유저 가입 여부 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));

        // 회원 권한 수정
        user.updateUserRole(UserRoleEnum.valueOf(updateRequestDto.getRole()));

        return UserInfoDto.of(user);
    }

    // 회원 탈퇴
    @Transactional
    @CacheEvict(cacheNames = {"userInfoCache", "userSearchInfoCache"}, allEntries = true)
    public String deleteUserInfo(Long userId) {
        // 조회 유저 가입 여부 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));

        // 논리적 회원 삭제
        user.delete(user.getEmail());

        return "[" + user.getUsername() + "] 회원님 탈퇴 완료.";
    }


    // 회원 검색
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userSearchInfoCache", key = "{#searchRequestDto.user_id, #searchRequestDto.role, #searchRequestDto.username, #searchRequestDto.email}")
    public Page<UserSearchResponseDto> searchUserInfo(Pageable pageable, UserSearchRequestDto searchRequestDto) {

        return userRepository.findAllUser(pageable, searchRequestDto);
    }
}
