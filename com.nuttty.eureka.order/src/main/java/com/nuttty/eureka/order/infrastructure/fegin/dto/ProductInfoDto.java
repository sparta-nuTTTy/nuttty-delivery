package com.nuttty.eureka.order.infrastructure.fegin.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductInfoDto {
    private Integer statusCode;
    private String resultMessage;
    private ProductDataDto data;
}
