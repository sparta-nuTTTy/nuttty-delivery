package com.nuttty.eureka.order.application.fegin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class HubDto {
    @JsonProperty("hub_id")
    private UUID hubId;
    @JsonProperty("user_id")
    private int userId;
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    @JsonProperty("created_at")
    private LocalDateTime created_at;
    @JsonProperty("updated_at")
    private LocalDateTime updated_at;
}