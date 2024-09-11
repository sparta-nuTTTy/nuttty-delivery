package com.nuttty.eureka.auth.application.dto;

import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class UserInfoDto implements Serializable {
    private Long user_id;
    private String username;
    private String email;
    private UserRoleEnum role;
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
                .created_at(user.getCreatedAt())
                .created_by(user.getCreatedBy())
                .updated_at(user.getUpdatedAt())
                .updated_by(user.getUpdatedBy())
                .build();
    }

    @QueryProjection
    public UserInfoDto(Long user_id, String username, String email, UserRoleEnum role, LocalDateTime created_at, String created_by, LocalDateTime updated_at, String updated_by){
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.created_at = created_at;
        this.created_by = created_by;
        this.updated_at = updated_at;
        this.updated_by = updated_by;
    }
}
