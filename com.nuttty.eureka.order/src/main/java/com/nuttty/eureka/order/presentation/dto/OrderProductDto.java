package com.nuttty.eureka.order.presentation.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderProductDto {
    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    class OrderItem {
        private UUID productId;
        private BigDecimal productPrice;
        private int orderAmount;
    }
}
