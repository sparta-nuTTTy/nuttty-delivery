package com.nuttty.eureka.order.presentation.dto;

import com.nuttty.eureka.order.domain.model.DeliveryStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public interface DeliveryDto {

    @Getter
    @Builder
    class DeliveryResponseDto {
        private UUID deliveryId;
        private UUID orderId;
        private Long deliveryPersonId;
        private UUID departureHubId;
        private UUID arrivalHubId;
        private String deliveryAddress;
        private String deliveryReceiver;
        private DeliveryStatus deliveryStatus;
        private List<UUID> deliveryRouteIds;
    }
}
