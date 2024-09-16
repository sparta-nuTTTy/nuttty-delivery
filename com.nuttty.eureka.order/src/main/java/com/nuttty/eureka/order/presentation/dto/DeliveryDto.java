package com.nuttty.eureka.order.presentation.dto;

import com.nuttty.eureka.order.domain.model.DeliveryStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

        @QueryProjection
        public DeliveryResponseDto(UUID deliveryId, UUID orderId, Long deliveryPersonId, UUID departureHubId, UUID arrivalHubId, String deliveryAddress, String deliveryReceiver, DeliveryStatus deliveryStatus, List<UUID> deliveryRouteIds) {
            this.deliveryId = deliveryId;
            this.orderId = orderId;
            this.deliveryPersonId = deliveryPersonId;
            this.departureHubId = departureHubId;
            this.arrivalHubId = arrivalHubId;
            this.deliveryAddress = deliveryAddress;
            this.deliveryReceiver = deliveryReceiver;
            this.deliveryStatus = deliveryStatus;
            this.deliveryRouteIds = deliveryRouteIds;
        }
    }

    @Getter
    @Builder
    class DeliverySaerch {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private UUID deliveryId;
        private UUID orderId;
        private UUID departureHubId;
        private String deliveryAddress;
        private DeliveryStatus deliveryStatus;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    class DeliveryUpdateRequestDto {
        private Long deliveryPersonId;
        private DeliveryStatus deliveryStatus;
    }

    @Getter
    @Builder
    class DeliveryUpdateResponseDto {
        private UUID deliveryId;
        private UUID orderId;
        private Long deliveryPersonId;
        private DeliveryStatus deliveryStatus;
    }
}
