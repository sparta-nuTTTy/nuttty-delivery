package com.nuttty.eureka.ai.application.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteDto {

    private UUID deliveryRouteId;
    private UUID deliveryId;
    private UUID hubRouteId;

    private String deliveryStatus;
    private Double estimatedDistance;
    private LocalDateTime estimatedTime;
    private Double realityDistance;
    private LocalDateTime realityTime;
}
