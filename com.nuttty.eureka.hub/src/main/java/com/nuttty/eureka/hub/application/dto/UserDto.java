package com.nuttty.eureka.hub.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long user_id;
    private String username;
    private String email;
    private String role;
}
