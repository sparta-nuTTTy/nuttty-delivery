package com.nuttty.eureka.ai.application.client;

import com.nuttty.eureka.ai.application.dto.delivery.DeliveryRequestDto;
import com.nuttty.eureka.ai.application.dto.order.OrderRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/api/v1/orders")
    OrderRequestDto findAllOrder(@RequestParam("startDate") LocalDateTime startDateTime,
                                 @RequestParam("endDate") LocalDateTime endDateTime,
                                 @RequestParam("size") int size,
                                 @RequestHeader("X-Forwarded-Port") String port);

    @GetMapping("/api/v1/deliveries")
    DeliveryRequestDto findAllDelivery(@RequestParam("startDate") LocalDateTime startDate,
                                       @RequestParam("endDate") LocalDateTime endDate,
                                       @RequestParam("size") int size,
                                       @RequestHeader("X-Forwarded-Port") String port);
}
