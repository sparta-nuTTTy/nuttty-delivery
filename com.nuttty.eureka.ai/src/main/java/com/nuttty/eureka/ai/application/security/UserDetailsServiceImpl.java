package com.nuttty.eureka.ai.application.security;

import com.nuttty.eureka.ai.application.client.DeliveryPersonClient;
import com.nuttty.eureka.ai.application.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final DeliveryPersonClient authClient;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 이메일 == unique Column
        UserDto user = authClient.findUserById(Long.valueOf(userId)).getBody();

        return new UserDetailsImpl(user);
    }
}



