package com.nuttty.eureka.company.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {

    private int status_code;
    private String result_message;
    private AuthDto data;
}
