package com.nuttty.eureka.company.application.client;

import com.nuttty.eureka.company.application.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/api/v1/auth/users/{user_id}/info")
    ResponseEntity<UserDto> findUserById(@PathVariable("user_id") Long userId);
}
