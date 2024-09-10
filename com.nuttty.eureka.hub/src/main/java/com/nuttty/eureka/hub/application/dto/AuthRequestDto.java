package com.nuttty.eureka.hub.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {

    private int status_code;
    private String result_message;
    private AuthDto authDto;
}
