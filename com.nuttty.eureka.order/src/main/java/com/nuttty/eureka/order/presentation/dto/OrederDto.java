package com.nuttty.eureka.order.presentation.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nuttty.eureka.order.domain.model.DeliveryStatus;
import com.nuttty.eureka.order.domain.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.nuttty.eureka.order.presentation.dto.OrderProductDto.*;

public interface OrederDto {
    @Getter
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    class OrderCreateDto {
        private UUID supplierId;
        private UUID receiverId;
        private List<OrderItem> productItems;
    }

    @Getter
    @Builder
    class OrderCreateResponseDto {
        private UUID orderId;
        private UUID supplierId;
        private UUID receiverId;
        private List<OrderItem> productItems;
    }

    @Getter
    @Builder
    class OrderResponseDto {
        private UUID orderId;
        private OrderStatus orderStatus;
        private BigDecimal orderTotalPrice;
        private UUID receiverId;
        private String receiverName;
        private UUID supplierId;
        private String supplierName;
        private UUID deliveryId;
        private String deliveryAddress;
        private DeliveryStatus deliveryStatus;
        private List<OrderItem> productItems;
        // 배송 순서 리스트
        private List<UUID> deliveryRoutes;
    }
}
