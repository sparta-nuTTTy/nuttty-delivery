package com.nuttty.eureka.order.application.security;

import com.nuttty.eureka.order.application.fegin.AuthClient;
import com.nuttty.eureka.order.application.fegin.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthClient authClient;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 이메일 == unique Column
        UserDto user = authClient.findUserById(Long.valueOf(userId), "19092").getBody();

        return new UserDetailsImpl(user);
    }
}



