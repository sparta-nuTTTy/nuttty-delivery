package com.nuttty.eureka.auth.application.service;

import com.nuttty.eureka.auth.application.dto.UserInfoDto;
import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원 상세 조회
    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo(Long loggedUserId, Long targetUserId) {
        // 조회 유저 가입 여부 검사
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 유저입니다."));
        // 로그인 유저 가입 여부 검사
        User loggedUser = userRepository.findById(loggedUserId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 유저입니다."));
        // 로그인 유저의 정보와 조회 할 유저의 정보 일치 검사 & 로그인 유저 권한 체크
        if(!loggedUserId.equals(targetUserId) && !loggedUser.getRole().equals(UserRoleEnum.MASTER)) {
            throw new IllegalArgumentException("해당 유저의 정보를 조회 할 권한이 없습니다.");
        }

        return UserInfoDto.of(targetUser);
    }
}
