package com.nuttty.eureka.auth.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryPersonTypeEnum {
    HUB_TRANSFER_PERSON("HUB_TRANSFER_PERSON"),
    COMPANY_DELIVERY_PERSON("COMPANY_DELIVERY_PERSON"),;

    private final String type;
}
