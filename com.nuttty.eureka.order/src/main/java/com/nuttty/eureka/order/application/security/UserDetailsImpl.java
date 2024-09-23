package com.nuttty.eureka.order.application.security;

import com.nuttty.eureka.order.application.feign.dto.UserDto;
import com.nuttty.eureka.order.application.feign.dto.UserRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails, Serializable {
    private final UserDto user;

    public UserDetailsImpl(UserDto user) {
        this.user = user;
    }

    public UserDto getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public Long getUserId() { return user.getUserId(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = user.getRole();
        String authority = role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }
}