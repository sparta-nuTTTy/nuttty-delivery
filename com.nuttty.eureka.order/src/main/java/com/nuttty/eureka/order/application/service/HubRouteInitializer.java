package com.nuttty.eureka.order.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Hub Route 초기화 클래스
 * Spring Boot 실행 시 초기화 작업을 수행합니다.
 */
@Component
@Slf4j(topic = "HubRouteInitializer")
@RequiredArgsConstructor
public class HubRouteInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final HubRouteService hubRouteService;

    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("허브 경로 초기화 시작");
        hubRouteService.createAllHubRoutes();
    }
}
