package com.nuttty.eureka.order.application.fegin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyInfoDto {
    private Integer statusCode;
    private String resultMessage;
    @JsonProperty("companyDto")
    private CompanyDataDto data;
}
