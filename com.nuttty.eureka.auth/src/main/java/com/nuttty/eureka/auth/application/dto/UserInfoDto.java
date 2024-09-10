package com.nuttty.eureka.auth.application.dto;

import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class UserInfoDto {
    private Long user_id;
    private String username;
    private String email;
    private UserRoleEnum role;
    private Boolean is_delete;
    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;


    public static UserInfoDto of(User user) {
        return UserInfoDto.builder()
                .user_id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .is_delete(user.getIsDelete())
                .created_at(user.getCreatedAt())
                .created_by(user.getCreatedBy())
                .updated_at(user.getUpdatedAt())
                .updated_by(user.getUpdatedBy())
                .build();
    }
}
