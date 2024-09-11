package com.nuttty.eureka.order.infrastructure.fegin.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDataDto {
    private UUID productId;
    private UUID hubId;
    private String productName;
    private int productQuantity;
    private BigDecimal productPrice;
}
