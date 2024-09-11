package com.nuttty.eureka.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponseDto {
    private List<UserInfoDto> userInfoList;
}
