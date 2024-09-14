package com.nuttty.eureka.order.presentation.controller;

import com.nuttty.eureka.order.application.service.OrderService;
import com.nuttty.eureka.order.presentation.dto.ResultResponse;
import com.nuttty.eureka.order.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.nuttty.eureka.order.presentation.dto.OrederDto.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    /**
     * 주문 생성
     * @param orderCreateDto: 주문 생성 요청 DTO
     * @param userId: 사용자 ID
     * @param role: 사용자 권한
     * @return: 주문 생성 응답 DTO
     */
    @PostMapping
    public ResultResponse<OrderCreateResponseDto> createOrder(
            @RequestBody OrderCreateDto orderCreateDto,
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Role") String role) {

        log.info("주문 생성 시도 중 | request: {}, loginUser: {}", orderCreateDto, userId);
        OrderCreateResponseDto orderCreateResponseDto = orderService.createOrder(orderCreateDto);

        log.info("주문 생성 성공 | response: {}", orderCreateResponseDto);
        return ResultResponse.<OrderCreateResponseDto>builder()
                .resultMessage(SuccessCode.INSERT_SUCCESS.getMessage())
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .data(orderCreateResponseDto)
                .build();
    }

    /**
     * 주문 단건 조회
     * @param orderId: 주문 ID
     * @param userId: 사용자 ID
     * @param role: 사용자 권한
     * @return: 주문 조회 응답 DTO
     */
    @GetMapping("/{order_id}")
    public ResultResponse<OrderResponseDto> getOrder(
            @PathVariable("order_id") UUID orderId,
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Role") String role) {

        log.info("주문 조회 시도 중 | orderId: {}", orderId);
        OrderResponseDto orderResponseDto = orderService.getOrder(orderId);

        log.info("주문 조회 성공 | response: {}", orderResponseDto);

        return ResultResponse.<OrderResponseDto>builder()
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .data(orderResponseDto)
                .build();
    }
}
