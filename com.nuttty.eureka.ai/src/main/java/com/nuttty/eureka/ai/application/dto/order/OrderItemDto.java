package com.nuttty.eureka.ai.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private UUID productId;
    private BigDecimal productPrice;
    private int orderAmount;
}
