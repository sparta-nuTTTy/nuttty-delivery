package com.nuttty.eureka.order.domain.repository;

import com.nuttty.eureka.order.presentation.dto.OrederDto.OrderResponseDto;
import com.nuttty.eureka.order.presentation.dto.OrederDto.OrderSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrderRepositoryCustom {
    Page<OrderResponseDto> findSearchOrders(OrderSearchDto condition, Pageable pageable);

}
