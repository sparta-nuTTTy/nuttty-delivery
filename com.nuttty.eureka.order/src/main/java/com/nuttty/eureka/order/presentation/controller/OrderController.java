package com.nuttty.eureka.order.presentation.controller;

import com.nuttty.eureka.order.application.security.UserDetailsImpl;
import com.nuttty.eureka.order.application.service.OrderService;
import com.nuttty.eureka.order.presentation.dto.ErrorResponse;
import com.nuttty.eureka.order.presentation.dto.ResultResponse;
import com.nuttty.eureka.order.util.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.nuttty.eureka.order.presentation.dto.OrederDto.*;

@Tag(name = "주문", description = "주문 등록, 수정, 조회, 삭제 API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    /**
     * 주문 생성
     * @param orderCreateDto: 주문 생성 요청 DTO
     * @param userDetails: 로그인 사용자 정보
     * @return: 주문 생성 응답 DTO
     */
    @PostMapping
    @Operation(summary = "주문 생성", description = "주문을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "주문 생성 성공")
    public ResultResponse<OrderCreateResponseDto> createOrder(
            @RequestBody OrderCreateDto orderCreateDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("주문 생성 시도 중 | request: {}, loginUser: {}", orderCreateDto, userDetails.getUserId());
        OrderCreateResponseDto orderCreateResponseDto = orderService.createOrder(orderCreateDto);

        log.info("주문 생성 성공 | response: {}", orderCreateResponseDto);
        return ResultResponse.<OrderCreateResponseDto>builder()
                .resultMessage(SuccessCode.INSERT_SUCCESS.getMessage())
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .data(orderCreateResponseDto)
                .build();
    }

    /**
     * 주문 조회
     * @param orderId: 주문 ID
     * @param userDetails: 로그인 사용자 정보
     * @return: 주문 조회 응답 DTO
     */
    @GetMapping("/{order_id}")
    @Operation(summary = "주문 조회", description = "주문을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "주문 조회 성공")
    public ResultResponse<OrderResponseDto> getOrder(
            @Parameter(description = "주문 ID", example = "UUID", required = true)
            @PathVariable("order_id") UUID orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

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
    @Operation(
            summary = "주문 검색 및 페이징 조회",
            description = "주문을 검색 및 페이징 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "startDate",
                            description = "검색 시작일",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "endDate",
                            description = "검색 종료일",
                            schema = @Schema(type = "integer", format = "int64")
                    ),
                    @Parameter(
                            name = "status",
                            description = "주문 상태",
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "orderId",
                            description = "주문 ID",
                            required = false,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "supplierId",
                            description = "공급 업체 ID",
                            required = false,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "receiverId",
                            description = "수령 업체 ID",
                            required = false,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "pageable",
                            description = "페이지 정보 (기본값: size=20)",
                            required = false,
                            schema = @Schema(implementation = Pageable.class)
                    ),

            },
            responses = {
            @ApiResponse(responseCode = "200", description = "주문 검색 및 페이징 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    @GetMapping
    public ResultResponse<Page<OrderResponseDto>> searchOrders(
            @Parameter(description = "주문 검색 조건", hidden = true)
            OrderSearchDto condition,
            @ParameterObject @PageableDefault(size = 20) Pageable Pageable) {
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
    @Operation(summary = "주문 소프트 삭제", description = "주문을 소프트 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "주문 소프트 삭제 성공")
    public ResultResponse<OrderCancelResponseDto> cancelOrder(
            @Parameter(description = "주문 ID", example = "UUID", required = true)
            @PathVariable("order_id") UUID orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("주문 취소 시도 중 | orderId: {}", orderId);
        OrderCancelResponseDto orderCancelResponseDto = orderService.cancelOrder(orderId, userDetails.getUser().getEmail());

        log.info("주문 취소 성공 | response: {}", orderCancelResponseDto);

        return ResultResponse.<OrderCancelResponseDto>builder()
                .resultMessage(SuccessCode.DELETE_SUCCESS.getMessage())
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .data(orderCancelResponseDto)
                .build();
    }

    /**
     * 주문 수정
     * @param orderId: 주문 ID
     * @param orderUpdateDto: 주문 수정 요청 DTO
     * @param userDetails: 로그인 사용자 정보
     * @return: 주문 수정 응답 DTO
     */
    @PatchMapping("/{order_id}")
    @Operation(summary = "주문 수정", description = "주문을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "주문 수정 성공")
    public ResultResponse<OrderUpdateResponseDto> updateOrder(
            @Parameter(description = "주문 ID", example = "UUID", required = true)
            @PathVariable("order_id") UUID orderId,
            @RequestBody OrderUpdateDto orderUpdateDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("주문 수정 시도 중 | orderId: {}, request: {}, loginUser: {}", orderId, orderUpdateDto, userDetails.getUserId());
        OrderUpdateResponseDto orderUpdateResponseDto = orderService.updateOrder(orderId, orderUpdateDto);

        log.info("주문 수정 성공 | response: {}", orderUpdateResponseDto);

        return ResultResponse.<OrderUpdateResponseDto>builder()
                .resultMessage(SuccessCode.UPDATE_SUCCESS.getMessage())
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .data(orderUpdateResponseDto)
                .build();
    }
}
