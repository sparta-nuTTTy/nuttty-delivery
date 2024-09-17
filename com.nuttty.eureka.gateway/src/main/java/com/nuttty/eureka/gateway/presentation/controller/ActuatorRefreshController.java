package com.nuttty.eureka.gateway.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/actuator")
public class ActuatorRefreshController {

    private final RestTemplate restTemplate;

    public ActuatorRefreshController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/refresh")
    public void refreshAllServices() {
        // 각 서비스의 /actuator/refresh 엔드포인트 호출
        String[] services = {
                "http://localhost:19097/actuator/refresh", // ai-service
                "http://localhost:19095/actuator/refresh", // company-service
                "http://localhost:19096/actuator/refresh", // hub-service

        };

        for (String serviceUrl : services) {
            restTemplate.postForObject(serviceUrl, null, String.class);
        }
    }
}
