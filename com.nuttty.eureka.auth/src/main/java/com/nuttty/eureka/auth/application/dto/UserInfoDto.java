package com.nuttty.eureka.auth.application.dto;

import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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


    public UserInfoDto(User user) {
        this.user_id = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.is_delete = user.getIsDelete();
        this.created_at = user.getCreatedAt();
        this.created_by = user.getCreatedBy();
        this.updated_at = user.getUpdatedAt();
        this.updated_by = user.getUpdatedBy();
    }
}
