package com.nuttty.eureka.company.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {

    private Long user_id;
    private String username;
    private String email;
    private String role;
}
