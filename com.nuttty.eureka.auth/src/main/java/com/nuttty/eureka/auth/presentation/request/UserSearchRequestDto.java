package com.nuttty.eureka.auth.presentation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchRequestDto {
    private Long user_id;
    private String role;
    private String username;
    private String email;
}
