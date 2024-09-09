package com.nuttty.eureka.auth.domain.model;

import com.nuttty.eureka.auth.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(value = {AuditingEntityListener.class})
public class User extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;


    // 유저 생성 - (회원 가입)
    public static User create(String username, String email, String password, UserRoleEnum role) {
        return User.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(role)
                .build();
    }
}
