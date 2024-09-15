package com.nuttty.eureka.ai.application.dto.order;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private UUID orderId;
    private String orderStatus;
    private BigDecimal orderTotalPrice;

    private UUID receiverId;
    private String receiverName;
    private UUID supplierId;
    private String supplierName;
    private UUID deliveryId;
    private String deliveryAddress;
    private String deliveryStatus;

    private List<OrderItemDto> orderItemDtoList;

    private List<UUID> deliveryRoutes;
}
