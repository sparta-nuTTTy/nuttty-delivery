package com.nuttty.eureka.auth.presentation.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryPersonTypeUpdateRequestDto {
    private String type;
    private UUID hubId;
}
