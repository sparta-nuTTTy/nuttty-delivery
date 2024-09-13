package com.nuttty.eureka.order.application.fegin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyDataDto {
    @JsonProperty("company_id")
    private UUID companyId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("hub_id")
    private UUID hubId;
    private String name;
    private String type;
    private String address;
}
