package com.nuttty.eureka.company.application.client;

import com.nuttty.eureka.company.application.dto.AuthRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/api/v1/users/{user_id}")
    AuthRequestDto findUserById(@RequestHeader("X-User-Id") Long loggedUserId,
                                @PathVariable("user_id") Long userId);
}
