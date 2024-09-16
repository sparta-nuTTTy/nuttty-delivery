package com.nuttty.eureka.ai.application.client;

import com.nuttty.eureka.ai.application.dto.hub.HubResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/v1/hubs/{hub_id}")
    HubResponseDto findByHubId(@PathVariable("hub_id")UUID hubId);
}
