package com.nuttty.eureka.order.presentation.controller;

import com.nuttty.eureka.order.application.service.DeliveryService;
import com.nuttty.eureka.order.presentation.dto.DeliveryDto;
import com.nuttty.eureka.order.presentation.dto.ResultResponse;
import com.nuttty.eureka.order.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.nuttty.eureka.order.presentation.dto.DeliveryDto.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    /**
     * 배송 단건 조회
     * @param deliveryId: 배송 ID
     * @param userId: 사용자 ID
     * @param role: 사용자 권한
     * @return: 배송 조회 응답 DTO
     */
    @GetMapping("/{deliveryId}")
    public ResultResponse<DeliveryResponseDto> getDelivery(
            @PathVariable("deliveryId") UUID deliveryId,
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Role") String role) {
        log.info("배송 단건 조회 시도 중 | deliveryId: {}, loginUser: {}", deliveryId, userId);

        DeliveryResponseDto deliveryResponseDto = deliveryService.getDelivery(deliveryId);

        return ResultResponse.<DeliveryResponseDto>builder()
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .data(deliveryResponseDto)
                .build();
    }

    // 배송 검색 및 페이징 조회
    @GetMapping
    public ResultResponse<Page<DeliveryResponseDto>> searchDeliveries(DeliverySaerch condition, Pageable pageable) {
        log.info("배송 검색 및 페이징 조회 시도 중 | condition: {}, pageable: {}", condition, pageable);
        Page<DeliveryResponseDto> deliveryResponseDtos = deliveryService.getDeliveries(condition, pageable);

        return ResultResponse.<Page<DeliveryResponseDto>>builder()
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .data(deliveryResponseDtos)
                .build();
    }
}
