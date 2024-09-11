package com.nuttty.eureka.auth.domain.repository;

import com.nuttty.eureka.auth.application.dto.UserSearchResponseDto;
import com.nuttty.eureka.auth.presentation.request.UserSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JpaUserRepository {
    Page<UserSearchResponseDto> findAllUser(Pageable pageable, UserSearchRequestDto searchRequestDto);
}
