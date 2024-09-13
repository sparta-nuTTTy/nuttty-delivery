package com.nuttty.eureka.order.application.fegin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDto {
    @JsonProperty("product_id")
    private UUID productId;
    @JsonProperty("hub_id")
    private UUID hubId;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("product_quantity")
    private int productQuantity;
    @JsonProperty("product_price")
    private BigDecimal productPrice;
}
