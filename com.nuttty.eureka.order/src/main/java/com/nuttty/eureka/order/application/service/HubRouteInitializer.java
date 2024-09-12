package com.nuttty.eureka.order.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Hub Route 초기화 클래스
 * Spring Boot 실행 시 초기화 작업을 수행한다.
 */
@Component
@Slf4j(topic = "HubRouteInitializer")
@RequiredArgsConstructor
public class HubRouteInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private final HubRouteService hubRouteService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 애플리케이션 시작 시 허브 경로 생성
        // 추후 허브 생성 쪽 시점에 허브 간 경로 생성 필요
        log.info("허브 경로 초기화 시작");
        hubRouteService.createAllHubRoutes();
    }
}
