package com.nuttty.eureka.hub.application.client;

import com.nuttty.eureka.hub.application.dto.AuthRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface UserClient {

    @GetMapping("/api/v1/users/{user_id}")
    AuthRequestDto findUserById(@PathVariable("user_id") Long userId);
}
