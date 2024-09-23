package com.nuttty.eureka.order.application.feign;

import com.nuttty.eureka.order.application.feign.dto.HubResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "hub-service")
public interface HubClient {
    // 허브 목록 조회
    @GetMapping("/api/v1/hubs")
    HubResponse getAllHubs(@RequestHeader("X-Forwarded-Port") String port);
}
