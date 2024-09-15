package com.nuttty.eureka.order.presentation.dto;

import com.nuttty.eureka.order.domain.model.DeliveryStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public interface DeliveryRouteDto {
    @Getter
    @Builder
    class DeliveryRouteResponseDto {
        private UUID deliveryRouteId;
        private UUID deliveryId;
        private DeliveryStatus deliveryStatus;
        private Long deliveryPersonId;
        private UUID departureHubId;
        private UUID arrivalHubId;
        private String routeInfo;
        private String estmatedDistance;
        private String estimatedTime;
    }

}
