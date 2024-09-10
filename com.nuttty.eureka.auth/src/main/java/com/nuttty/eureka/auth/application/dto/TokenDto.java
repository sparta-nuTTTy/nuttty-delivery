package com.nuttty.eureka.auth.application.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class TokenDto {
    private static final String BEARER_PREFIX = "Bearer ";
    private String accessToken;

    public static TokenDto of(String accessToken) {
        return TokenDto.builder().accessToken(BEARER_PREFIX + accessToken).build();
    }

    @Override
    public String toString() {
        return accessToken;
    }
}