package com.nuttty.eureka.order.presentation.controller;

import com.nuttty.eureka.order.application.security.UserDetailsImpl;
import com.nuttty.eureka.order.application.service.DeliveryRouteService;
import com.nuttty.eureka.order.presentation.dto.DeliveryRouteDto.DeliveryRouteResponseDto;
import com.nuttty.eureka.order.presentation.dto.ResultResponse;
import com.nuttty.eureka.order.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery-routes")
public class DeliveryRouteController {
    private final DeliveryRouteService deliveryRouteService;

    /**
     * 배송 경로 단건 조회
     * @param deliveryRouteId: 배송 경로 ID
     * @param userId: 사용자 ID
     * @param role: 사용자 권한
     * @return: 배송 경로 조회 응답 DTO
     */
    @GetMapping("/{deliveryRouteId}")
    public ResultResponse<DeliveryRouteResponseDto> getDeliveryRoutes(
            @PathVariable("deliveryRouteId") UUID deliveryRouteId,
            @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        log.info("배송 경로 단건 조회 시도 중 | deliveryRouteId: {}, loginUser: {}", deliveryRouteId, userDetails.getUserId());

        DeliveryRouteResponseDto deliveryRouteResponseDto = deliveryRouteService.getDeliveryRoute(deliveryRouteId);

        return ResultResponse.<DeliveryRouteResponseDto>builder()
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .data(deliveryRouteResponseDto)
                .build();
    }

//    /**
//     * 배송 경로 수정
//     * @param deliveryRouteId: 배송 경로 ID
//     * @param deliveryRouteUpdateRequestDto: 배송 경로 수정 요청 DTO delivery_person_id: 배송자 ID, delivery_status: 배송 상태
//     * @param userId: 사용자 ID
//     * @param role: 사용자 권한
//     * @return: 배송 경로 수정 응답 DTO
//     */
//    @PatchMapping("/{deliveryRouteId}")
//    public ResultResponse<DeliveryRouteUpdateResponseDto> updateDeliveryRoute(
//            @PathVariable("deliveryRouteId") UUID deliveryRouteId,
//            @RequestBody DeliveryRouteUpdateRequestDto deliveryRouteUpdateRequestDto,
//            @RequestHeader(value = "X-User-Id") Long userId,
//            @RequestHeader(value = "X-User-Role") String role) {
//        log.info("배송 경로 수정 시도 중 | deliveryRouteId: {}, loginUser: {}", deliveryRouteId, userId);
//
//        DeliveryRouteUpdateResponseDto deliveryRouteUpdateResponseDto = deliveryRouteService.updateDeliveryRoute(deliveryRouteId, deliveryRouteUpdateRequestDto);
//
//        return ResultResponse.<DeliveryRouteUpdateResponseDto>builder()
//                .resultMessage(SuccessCode.UPDATE_SUCCESS.getMessage())
//                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
//                .data(deliveryRouteUpdateResponseDto)
//                .build();
//    }
}
