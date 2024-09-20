package com.nuttty.eureka.company.application.security;

import com.nuttty.eureka.company.application.client.AuthClient;
import com.nuttty.eureka.company.application.dto.UserDto;
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
        UserDto user = authClient.findUserById(Long.valueOf(userId)).getBody();

        return new UserDetailsImpl(user);
    }
}



