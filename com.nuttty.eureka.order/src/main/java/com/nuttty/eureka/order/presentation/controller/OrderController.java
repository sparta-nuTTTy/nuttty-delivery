package com.nuttty.eureka.order.presentation.controller;

import com.nuttty.eureka.order.application.service.OrderService;
import com.nuttty.eureka.order.presentation.dto.ResultResponse;
import com.nuttty.eureka.order.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    /**
     * 주문 동적 검색 및 페이징 조회
     * @param condition: 주문 검색 조건
     *                 - startDate: 주문 생성 시작일 <br>
     *                 - endDate: 주문 생성 종료일 <br>
     *                 - status: 주문 상태 <br>
     *                 - orderId: 주문 ID <br>
     *                 - supplierId: 공급자 ID <br>
     *                 - receiverId: 수신자 ID <br>
     * @param Pageable: 페이징 정보
     *                - page: 페이지 번호 <br>
     *                - size: 페이지 크기 <br>
     *                - sort: 정렬 조건 createdAt, updatedAt <br>
     *                - direction: 정렬 방향 ASC, DESC <br>
     */
    @GetMapping
    public ResultResponse<Page<OrderResponseDto>> searchOrders(OrderSearchDto condition, @PageableDefault(size = 20) Pageable Pageable) {
        log.info("주문 검색 및 페이징 조회 시도 중 | condition: {}, pageable: {}", condition, Pageable);

        Page<OrderResponseDto> orderResponseDtos = orderService.searchOrders(condition, Pageable);

        log.info("주문 검색 및 페이징 조회 성공 | response: {}", orderResponseDtos);

        return ResultResponse.<Page<OrderResponseDto>>builder()
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .data(orderResponseDtos)
                .build();
    }

    /**
     * 주문 소프트 삭제
     * @param orderId: 주문 ID
     * @param userId: 사용자 ID
     * @param role: 사용자 권한
     * @param email: 사용자 이메일
     * @return: 주문 소프트 삭제 응답 DTO
     */
    @DeleteMapping("/{order_id}")
    public ResultResponse<OrderCancelResponseDto> cancelOrder(
            @PathVariable("order_id") UUID orderId,
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Role") String role,
            @RequestHeader(value = "X-User-Email") String email
    ) {

        log.info("주문 취소 시도 중 | orderId: {}", orderId);
        OrderCancelResponseDto orderCancelResponseDto = orderService.cancelOrder(orderId, email);

        log.info("주문 취소 성공 | response: {}", orderCancelResponseDto);

        return ResultResponse.<OrderCancelResponseDto>builder()
                .resultMessage(SuccessCode.DELETE_SUCCESS.getMessage())
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .data(orderCancelResponseDto)
                .build();
    }

    @PatchMapping("/{order_id}")
    public ResultResponse<OrderUpdateResponseDto> updateOrder(
            @PathVariable("order_id") UUID orderId,
            @RequestBody OrderUpdateDto orderUpdateDto,
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Role") String role
    ) {
        log.info("주문 수정 시도 중 | orderId: {}, request: {}, loginUser: {}", orderId, orderUpdateDto, userId);
        OrderUpdateResponseDto orderUpdateResponseDto = orderService.updateOrder(orderId, orderUpdateDto);

        log.info("주문 수정 성공 | response: {}", orderUpdateResponseDto);

        return ResultResponse.<OrderUpdateResponseDto>builder()
                .resultMessage(SuccessCode.UPDATE_SUCCESS.getMessage())
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .data(orderUpdateResponseDto)
                .build();
    }
}
