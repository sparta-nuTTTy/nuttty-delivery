package com.nuttty.eureka.gateway.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomHeaderDto {
    private String token;
    private String userId;
    private String role;
}
