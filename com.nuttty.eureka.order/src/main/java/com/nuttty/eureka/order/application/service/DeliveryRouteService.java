package com.nuttty.eureka.order.application.service;

import com.nuttty.eureka.order.domain.model.DeliveryRoute;
import com.nuttty.eureka.order.domain.model.Order;
import com.nuttty.eureka.order.infrastructure.repository.OrderRepository;
import com.nuttty.eureka.order.presentation.dto.DeliveryRouteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j(topic = "DeliveryRouteService")
@RequiredArgsConstructor
public class DeliveryRouteService {
    private final OrderRepository orderRepository;

    public DeliveryRouteDto.DeliveryRouteResponseDto getDeliveryRoute(UUID deliveryRouteId) {
        log.info("배송 경로 단건 조회 시작 | deliveryRouteId: {}", deliveryRouteId);

        Order order = orderRepository.findOrderByDeliveryRouteId(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("배송 경로가 존재하지 않습니다."));

        // 배송 경로 필터링
        DeliveryRoute deliveryRoute = order.getDelivery().getDeliveryRoutes().stream()
                .filter(route -> route.getDeliveryRouteId().equals(deliveryRouteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("배송 경로가 존재하지 않습니다."));

        // DeliveryRouteDto 변환
        return DeliveryRouteDto.DeliveryRouteResponseDto.builder()
                .deliveryRouteId(deliveryRoute.getDeliveryRouteId())
                .deliveryId(deliveryRoute.getDelivery().getDeliveryId())
                .deliveryStatus(deliveryRoute.getDelivery().getDeliveryStatus())
                .deliveryPersonId(deliveryRoute.getDeliveryPersonId())
                .departureHubId(deliveryRoute.getHubRoute().getDepartureHubId())
                .arrivalHubId(deliveryRoute.getHubRoute().getArrivalHubId())
                .routeInfo(deliveryRoute.getHubRoute().getRouteInfo())
                .estmatedDistance("허브 간 이동 정보 외부 API 연동 후 계산")
                .estimatedTime("허브 간 이동 정보 외부 API 연동 후 계산")
                .build();
    }

}
