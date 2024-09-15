package com.nuttty.eureka.ai.application.client;

import com.nuttty.eureka.ai.application.dto.deliveryperson.DeliveryPersonRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service")
public interface DeliveryPersonClient {

    @GetMapping("/api/v1/delivery_people")
    DeliveryPersonRequestDto findAllCommonDeliveryPerson(@RequestParam("size") int size,
                                                         @RequestParam("delivery_person_type") String deliveryType);
}
