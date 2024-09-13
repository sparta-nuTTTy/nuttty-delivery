package com.nuttty.eureka.auth.application.client;

import com.nuttty.eureka.auth.presentation.request.HubRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/v1/hubs/{hub_id}")
    ResponseEntity<HubRequestDto> findOneHub(@PathVariable("hub_id") UUID hubId);
}
