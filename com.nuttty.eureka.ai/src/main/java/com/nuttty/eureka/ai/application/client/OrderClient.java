package com.nuttty.eureka.ai.application.client;

import com.nuttty.eureka.ai.application.dto.order.OrderRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/api/v1/orders")
    OrderRequestDto findAllOrder(@RequestParam("startDate") LocalDateTime startDateTime,
                                 @RequestParam("endDate") LocalDateTime endDateTime,
                                 @RequestParam("size") int size);
}
