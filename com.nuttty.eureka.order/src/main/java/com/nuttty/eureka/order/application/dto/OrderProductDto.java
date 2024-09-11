package com.nuttty.eureka.order.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderProductDto {
    @Data
    @Builder
    class OrderItem {
        private UUID productId;
        private BigDecimal productPrice;
        private int orderAmount;
    }
}
