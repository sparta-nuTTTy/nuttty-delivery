package com.nuttty.eureka.order.presentation.controller;

import com.nuttty.eureka.order.application.service.OrderService;
import com.nuttty.eureka.order.presentation.dto.ResultResponse;
import com.nuttty.eureka.order.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.nuttty.eureka.order.presentation.dto.OrederDto.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResultResponse<OrderResponseDto> createOrder(
            @RequestBody OrderCreateDto orderCreateDto,
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Role") String role) {

        log.info("주문 생성 시도 중 | request: {}, loginUser: {}", orderCreateDto, userId);
        OrderResponseDto orderResponseDto = orderService.createOrder(orderCreateDto);

        log.info("주문 생성 성공 | response: {}", orderResponseDto);
        return ResultResponse.<OrderResponseDto>builder()
                .data(orderResponseDto)
                .resultMessage(SuccessCode.INSERT_SUCCESS.getMessage())
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .build();
    }
}
