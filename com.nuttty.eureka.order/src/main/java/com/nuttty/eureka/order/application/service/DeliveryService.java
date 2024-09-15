package com.nuttty.eureka.order.application.service;

import com.nuttty.eureka.order.domain.model.DeliveryRoute;
import com.nuttty.eureka.order.domain.model.Order;
import com.nuttty.eureka.order.infrastructure.repository.OrderRepository;
import com.nuttty.eureka.order.presentation.dto.DeliveryDto.DeliveryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j(topic = "DeliveryService")
@RequiredArgsConstructor
public class DeliveryService {
    private final OrderRepository orderRepository;

    // 배송 단건 조회
    @Transactional(readOnly = true)
    public DeliveryResponseDto getDelivery(UUID deliveryId) {
        log.info("배송 단건 조회 시작 | deliveryId: {}", deliveryId);

        Order order = orderRepository.findOrderByDeliveryId(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배송이 존재하지 않습니다."));

        return DeliveryResponseDto.builder()
                .deliveryId(order.getDelivery().getDeliveryId())
                .orderId(order.getOrderId())
                .deliveryPersonId(order.getDelivery().getDeliveryPersonId())
                .departureHubId(order.getDelivery().getDepartureHubId())
                .arrivalHubId(order.getDelivery().getArrivalHubId())
                .deliveryAddress(order.getDelivery().getDeliveryAddress())
                .deliveryReceiver(order.getDelivery().getDeliveryReceiver())
                .deliveryStatus(order.getDelivery().getDeliveryStatus())
                .deliveryRouteIds(order.getDelivery().getDeliveryRoutes().stream()
                        .map(DeliveryRoute::getDeliveryRouteId)
                        .toList())
                .build();
    }
}
