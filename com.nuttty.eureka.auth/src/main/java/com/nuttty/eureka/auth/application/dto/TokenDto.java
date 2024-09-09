package com.nuttty.eureka.auth.application.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class TokenDto {
    private static String bearerPrefix = "Bearer ";
    private String accessToken;

    public static TokenDto of(String accessToken) {
        return TokenDto.builder().accessToken(bearerPrefix + accessToken).build();
    }

    @Override
    public String toString() {
        return accessToken;
    }
}