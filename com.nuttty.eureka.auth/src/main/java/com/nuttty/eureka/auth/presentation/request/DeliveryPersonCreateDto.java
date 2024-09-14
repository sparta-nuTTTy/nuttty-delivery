package com.nuttty.eureka.auth.presentation.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryPersonCreateDto {
    private Long userId;
    private UUID hubId;
    private String deliveryPersonType;
    private String slackId;

    public void setHubId(UUID hubId) {this.hubId = hubId;}
}
