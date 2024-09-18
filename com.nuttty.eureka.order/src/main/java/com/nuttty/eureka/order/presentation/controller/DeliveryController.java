package com.nuttty.eureka.order.presentation.controller;

import com.nuttty.eureka.order.application.security.UserDetailsImpl;
import com.nuttty.eureka.order.application.service.DeliveryService;
import com.nuttty.eureka.order.presentation.dto.ResultResponse;
import com.nuttty.eureka.order.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @PathVariable("deliveryId") UUID deliveryId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("배송 단건 조회 시도 중 | deliveryId: {}, loginUser: {}", deliveryId, userDetails.getUserId());

        DeliveryResponseDto deliveryResponseDto = deliveryService.getDelivery(deliveryId);

        return ResultResponse.<DeliveryResponseDto>builder()
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .data(deliveryResponseDto)
                .build();
    }

    /**
     * 배송 검색 및 페이징 조회
     * @param condition 검색 조건 <br>
     *                  - deliveryId: 배송 ID <br>
     *                  - orderId: 주문 ID <br>
     *                  - departureHubId: 출발지 허브 ID <br>
     *                  - deliveryAddress: 배송 주소 <br>
     *                  - deliveryStatus: 배송 상태 <br>
     *                  - startDate: 검색 시작일 <br>
     *                  - endDate: 검색 종료일 <br>
     * @param pageable: 페이징 정보
     * @return: 배송 검색 및 페이징 조회 응답 DTO
     */
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

    /**
     * 배송 수정(배송 상태, 배송자 ID)
     * @param deliveryId: 배송 ID
     * @param deliveryUpdateRequestDto: 배송 수정 요청 DTO <br>
     *                                - deliveryPersonId: 배송자 ID <br>
     *                                - deliveryStatus: 배송 상태 <br>
     * @param userId: 사용자 ID
     * @param role: 사용자 권한
     * @return: 배송 수정 응답 DTO
     */
    @PatchMapping("/{deliveryId}")
    public ResultResponse<DeliveryUpdateResponseDto> updateDelivery(
            @PathVariable("deliveryId") UUID deliveryId,
            @RequestBody DeliveryUpdateRequestDto deliveryUpdateRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("배송 수정 시도 중 | deliveryId: {}, request: {}, loginUser: {}", deliveryId, deliveryUpdateRequestDto, userDetails.getUserId());

        // 배송 수정 로직
        DeliveryUpdateResponseDto deliveryUpdateResponseDto = deliveryService.updateDelivery(deliveryId, deliveryUpdateRequestDto);

        return ResultResponse.<DeliveryUpdateResponseDto>builder()
                .resultMessage(SuccessCode.UPDATE_SUCCESS.getMessage())
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .data(deliveryUpdateResponseDto)
                .build();
    }
}
