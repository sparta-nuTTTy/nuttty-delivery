package com.nuttty.eureka.order.application.fegin.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyDataDto {
    private UUID company;
    private UUID userId;
    private UUID hubId;
    private String name;
    private String type;
    private String address;
}
