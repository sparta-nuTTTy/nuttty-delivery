package com.nuttty.eureka.order.application.service;

import com.nuttty.eureka.order.domain.model.DeliveryRoute;
import com.nuttty.eureka.order.domain.model.Order;
import com.nuttty.eureka.order.exception.exceptionsdefined.OrderNotFoundException;
import com.nuttty.eureka.order.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.nuttty.eureka.order.presentation.dto.DeliveryRouteDto.*;

@Service
@Slf4j(topic = "DeliveryRouteService")
@RequiredArgsConstructor
public class DeliveryRouteService {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public DeliveryRouteResponseDto getDeliveryRoute(UUID deliveryRouteId) {
        log.info("배송 경로 단건 조회 시작 | deliveryRouteId: {}", deliveryRouteId);

        Order order = orderRepository.findOrderByDeliveryRouteId(deliveryRouteId)
                .orElseThrow(() -> new OrderNotFoundException("배송 경로가 존재하지 않습니다."));

        // 배송 경로 필터링
        DeliveryRoute deliveryRoute = order.getDelivery().getDeliveryRoutes().stream()
                .filter(route -> route.getDeliveryRouteId().equals(deliveryRouteId))
                .findFirst()
                .orElseThrow(() -> new OrderNotFoundException("배송 경로가 존재하지 않습니다."));

        // DeliveryRouteDto 변환
        return DeliveryRouteResponseDto.builder()
                .deliveryRouteId(deliveryRoute.getDeliveryRouteId())
                .deliveryId(deliveryRoute.getDelivery().getDeliveryId())
                .deliveryStatus(deliveryRoute.getDelivery().getDeliveryStatus())
                .deliveryPersonId(deliveryRoute.getDelivery().getDeliveryPersonId())
                .departureHubId(deliveryRoute.getHubRoute().getDepartureHubId())
                .arrivalHubId(deliveryRoute.getHubRoute().getArrivalHubId())
                .routeInfo(deliveryRoute.getHubRoute().getRouteInfo())
                // meters -> kilometers
                .estmatedDistance(String.format("%.2f KM", deliveryRoute.getHubRoute().getDistanceInKilometers() / 1000.0))
                // millisecond -> hours and minutes
                .estimatedTime(String.format("%d시간 %d분",
                        (int) (deliveryRoute.getHubRoute().getDuration() / 3600000),
                        (int) (deliveryRoute.getHubRoute().getDuration() / 60000) % 60))
                .build();
    }

//    @Transactional
//    public DeliveryRouteUpdateResponseDto updateDeliveryRoute(UUID deliveryRouteId, DeliveryRouteUpdateRequestDto deliveryRouteUpdateRequestDto) {
//        log.info("배송 경로 수정 시작 | deliveryRouteId: {}", deliveryRouteId);
//
//        Order order = orderRepository.findOrderByDeliveryRouteId(deliveryRouteId)
//                .orElseThrow(() -> new IllegalArgumentException("배송 경로가 존재하지 않습니다."));
//
//        // 배송 경로 필터링
//        DeliveryRoute deliveryRoute = order.getDelivery().getDeliveryRoutes().stream()
//                .filter(route -> route.getDeliveryRouteId().equals(deliveryRouteId))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("배송 경로가 존재하지 않습니다."));
//
//        // 배송 상태 및 배송 담당자 업데이트 (Null 체크를 통해 값이 존재할 때만 수정)
//        if (deliveryRouteUpdateRequestDto.getDeliveryPersonId() != null) {
//            deliveryRoute.updateDeliveryPerson(deliveryRouteUpdateRequestDto.getDeliveryPersonId());
//        }
//        if (deliveryRouteUpdateRequestDto.getDeliveryStatus() != null) {
//            deliveryRoute.getDelivery().changeDeliveryStatus(deliveryRouteUpdateRequestDto.getDeliveryStatus());
//        }
//
//        return DeliveryRouteUpdateResponseDto.builder()
//                .deliveryRouteId(deliveryRoute.getDeliveryRouteId())
//                .deliveryId(deliveryRoute.getDelivery().getDeliveryId())
//                .deliveryStatus(deliveryRoute.getDelivery().getDeliveryStatus())
//                .deliveryPersonId(deliveryRoute.getDeliveryPersonId())
//                .build();
//    }
}
