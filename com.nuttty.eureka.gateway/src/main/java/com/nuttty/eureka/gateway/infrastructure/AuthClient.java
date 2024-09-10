package com.nuttty.eureka.gateway.infrastructure;

import com.nuttty.eureka.gateway.application.service.AuthService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service")
public interface AuthClient extends AuthService {
    @GetMapping("/api/v1/auth/verify") // 유저 검증 API
    Boolean verifyUser(@RequestParam(value = "user_id") Long userId);
}
