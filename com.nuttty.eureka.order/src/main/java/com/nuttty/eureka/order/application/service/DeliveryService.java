package com.nuttty.eureka.order.application.service;

import com.nuttty.eureka.order.domain.model.DeliveryRoute;
import com.nuttty.eureka.order.domain.model.Order;
import com.nuttty.eureka.order.exception.exceptionsdefined.OrderNotFoundException;
import com.nuttty.eureka.order.infrastructure.repository.OrderRepository;
import com.nuttty.eureka.order.presentation.dto.DeliveryDto.DeliveryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.nuttty.eureka.order.presentation.dto.DeliveryDto.*;

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
                .orElseThrow(() -> new OrderNotFoundException("배송 경로가 존재하지 않습니다."));

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

    @Transactional(readOnly = true)
    public Page<DeliveryResponseDto> getDeliveries(DeliverySaerch condition, Pageable pageable) {
        log.info("배송 검색 및 페이징 조회 시작 | condition: {}, pageable: {}", condition, pageable);

        return orderRepository.findOrdersByCondition(condition, pageable);
    }

    // 배송 수정
    @Transactional
    public DeliveryUpdateResponseDto updateDelivery(UUID deliveryId, DeliveryUpdateRequestDto deliveryUpdateRequestDto) {
        log.info("배송 수정 시작 | deliveryId: {}, deliveryUpdateRequestDto: {}", deliveryId, deliveryUpdateRequestDto);

        Order order = orderRepository.findOrderByDeliveryId(deliveryId)
                .orElseThrow(() -> new OrderNotFoundException("배송 경로가 존재하지 않습니다."));

        if (deliveryUpdateRequestDto.getDeliveryStatus() != null) {
            order.getDelivery().changeDeliveryStatus(deliveryUpdateRequestDto.getDeliveryStatus());
        }

        if (deliveryUpdateRequestDto.getDeliveryPersonId() != null) {
            order.getDelivery().changeDeliveryPersonId(deliveryUpdateRequestDto.getDeliveryPersonId());
        }

        return DeliveryUpdateResponseDto.builder()
                .deliveryId(order.getDelivery().getDeliveryId())
                .orderId(order.getOrderId())
                .deliveryPersonId(order.getDelivery().getDeliveryPersonId())
                .deliveryStatus(order.getDelivery().getDeliveryStatus())
                .build();
    }
}
