package com.nuttty.eureka.order.application.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/api/v1/auth/users/{user_id}/info")
    ResponseEntity<UserDto> findUserById(@PathVariable("user_id") Long userId, @RequestHeader("X-Forwarded-Port") String port);
}