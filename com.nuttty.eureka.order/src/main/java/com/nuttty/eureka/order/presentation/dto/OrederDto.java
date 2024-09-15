package com.nuttty.eureka.order.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nuttty.eureka.order.domain.model.DeliveryStatus;
import com.nuttty.eureka.order.domain.model.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.nuttty.eureka.order.presentation.dto.OrderProductDto.OrderItem;

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
    class OrderSearchDto {
        LocalDateTime startDate;
        LocalDateTime endDate;
        UUID orderId;
        UUID supplierId;
        UUID receiverId;
        String status;
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

        @QueryProjection
        public OrderResponseDto(UUID orderId, OrderStatus orderStatus, BigDecimal orderTotalPrice, UUID receiverId, String receiverName, UUID supplierId, String supplierName, UUID deliveryId, String deliveryAddress, DeliveryStatus deliveryStatus, List<OrderItem> productItems, List<UUID> deliveryRoutes) {
            this.orderId = orderId;
            this.orderStatus = orderStatus;
            this.orderTotalPrice = orderTotalPrice;
            this.receiverId = receiverId;
            this.receiverName = receiverName;
            this.supplierId = supplierId;
            this.deliveryId = deliveryId;
            this.deliveryAddress = deliveryAddress;
            this.deliveryStatus = deliveryStatus;
            this.productItems = productItems;
            this.deliveryRoutes = deliveryRoutes;
        }
    }

    @Getter
    @Builder
    class OrderCancelResponseDto {
        private UUID orderId;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    class OrderUpdateDto {
        @JsonProperty("order_status")
        private OrderStatus orderStatus;
    }

    @Getter
    @Builder
    class OrderUpdateResponseDto {
        private UUID orderId;
        private OrderStatus orderStatus;
    }

}
