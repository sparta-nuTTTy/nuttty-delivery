package com.nuttty.eureka.order.domain.repository;

import com.nuttty.eureka.order.presentation.dto.DeliveryDto.DeliveryResponseDto;
import com.nuttty.eureka.order.presentation.dto.OrederDto.OrderResponseDto;
import com.nuttty.eureka.order.presentation.dto.OrederDto.OrderSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.nuttty.eureka.order.presentation.dto.DeliveryDto.*;


public interface OrderRepositoryCustom {
    Page<OrderResponseDto> findSearchOrders(OrderSearchDto condition, Pageable pageable);

    Page<DeliveryResponseDto> findOrdersByCondition(DeliverySaerch condition, Pageable pageable);
}
