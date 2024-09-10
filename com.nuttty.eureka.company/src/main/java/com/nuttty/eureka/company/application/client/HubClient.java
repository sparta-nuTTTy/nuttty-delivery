package com.nuttty.eureka.company.application.client;

import com.nuttty.eureka.company.application.dto.HubRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/v1/hubs/{hub_id}")
    HubRequestDto findByHubId(@PathVariable("hub_id") UUID hub_id);
}
