package com.nuttty.eureka.hub.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {

    // API 응답 결과
    private AuthDto data;

    // API 응답 코드
    private int resultCode;

    // API 응답 메시지
    private String resultMessage;
}
