package com.nuttty.eureka.ai.application.client;

import com.nuttty.eureka.ai.application.dto.UserDto;
import com.nuttty.eureka.ai.application.dto.deliveryperson.DeliveryPersonRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service")
public interface DeliveryPersonClient {

    @GetMapping("/api/v1/delivery-people/search")
    DeliveryPersonRequestDto findAllCommonDeliveryPerson(@RequestParam("size") int size,
                                                         @RequestParam("type") String type,
                                                         @RequestHeader("X-User-Role") String role,
                                                         @RequestHeader("X-User-Id") Long userId);

    @GetMapping("/api/v1/delivery-people/search")
    DeliveryPersonRequestDto findAllCompanyDeliveryPerson(@RequestParam("size") int size,
                                                          @RequestParam("type") String type,
                                                          @RequestHeader("X-User-Role") String role,
                                                          @RequestHeader("X-User-Id") Long userId);

    @GetMapping("/api/v1/auth/users/{user_id}/info")
    ResponseEntity<UserDto> findUserById(@PathVariable("user_id") Long userId);
}
