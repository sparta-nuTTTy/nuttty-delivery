package com.nuttty.eureka.ai.application.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    private UUID deliveryId;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private Long deliveryPersonId;

    private String deliveryAddress;
    private String deliveryReceiver;
    private String deliveryStatus;

    private List<DeliveryRouteDto> routeList;
}
