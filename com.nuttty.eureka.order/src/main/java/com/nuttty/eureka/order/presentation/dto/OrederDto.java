package com.nuttty.eureka.order.presentation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

import static com.nuttty.eureka.order.presentation.dto.OrderProductDto.*;

public interface OrederDto {
    @Getter
    @Builder
    class OrderCreateDto {
        private UUID supplierId;
        private UUID receiverId;
        private List<OrderItem> productItems;
    }

    @Getter
    @Builder
    class OrderResponseDto {
        private UUID orderId;
        private UUID supplierId;
        private UUID receiverId;
        private List<OrderItem> productItems;
    }
}
