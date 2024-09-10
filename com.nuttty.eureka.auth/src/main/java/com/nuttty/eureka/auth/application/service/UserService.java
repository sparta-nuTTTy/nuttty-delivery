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
        // 유저 등록 여부 검사
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 유저입니다."));
        // 로그인한 유저의 정보와 조회하려는 유저의 정보 일치 검사 & 권한체크
        if(!loggedUserId.equals(targetUserId) && !user.getRole().equals(UserRoleEnum.MASTER)) {
            throw new IllegalArgumentException("해당 유저의 정보를 조회 할 권한이 없습니다.");
        }

        return UserInfoDto.of(user);
    }
}
