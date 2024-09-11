package com.nuttty.eureka.order.application.fegin;

import com.nuttty.eureka.order.application.fegin.dto.HubResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "hub-serivce")
public interface HubClient {
    // 허브 목록 조회
    @GetMapping("/api/v1/hubs")
    HubResponse getAllHubs();
}
