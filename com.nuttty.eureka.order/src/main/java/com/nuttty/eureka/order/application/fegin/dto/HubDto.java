package com.nuttty.eureka.order.application.fegin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class HubDto {
    private UUID hubId;
    private int userId;
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}