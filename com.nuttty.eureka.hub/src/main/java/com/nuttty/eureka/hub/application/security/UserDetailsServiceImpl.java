package com.nuttty.eureka.hub.application.security;

import com.nuttty.eureka.hub.application.client.AuthClient;
import com.nuttty.eureka.hub.application.dto.UserDto;
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
        UserDto user = authClient.findUserById(Long.valueOf(userId), "19092").getBody();

        return new UserDetailsImpl(user);
    }
}