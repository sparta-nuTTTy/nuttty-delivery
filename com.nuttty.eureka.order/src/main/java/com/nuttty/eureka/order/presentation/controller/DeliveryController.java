package com.nuttty.eureka.order.presentation.controller;

import com.nuttty.eureka.order.application.security.UserDetailsImpl;
import com.nuttty.eureka.order.application.service.DeliveryService;
import com.nuttty.eureka.order.presentation.dto.ResultResponse;
import com.nuttty.eureka.order.util.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.nuttty.eureka.order.presentation.dto.DeliveryDto.*;

@Tag(name = "배송", description = "배송 조회, 수정 API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    /**
     * 배송 단건 조회
     * @param deliveryId: 배송 ID
     * @param userDetails: 로그인 사용자 정보
     * @return: 배송 단건 조회 응답 DTO
     */
    @GetMapping("/{deliveryId}")
    @Operation(summary = "배송 단건 조회", description = "배송 단건 조회합니다.")
    @ApiResponse(responseCode = "200", description = "배송 단건 조회 성공")
    public ResultResponse<DeliveryResponseDto> getDelivery(
            @Parameter(description = "배송 ID")
            @PathVariable("deliveryId") UUID deliveryId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
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
    @Operation(
            summary = "배송 검색 및 페이징 조회",
            description = "배송을 검색 및 페이징 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "deliveryId",
                            description = "배송 ID",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "orderId",
                            description = "주문 ID",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "departureHubId",
                            description = "출발 허브 ID",
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "deliveryAddress",
                            description = "배송 주소",
                            required = false,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "deliveryStatus",
                            description = "배송 상태",
                            required = false,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "startDate",
                            description = "검색 시작일",
                            required = false,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "endDate",
                            description = "검색 종료일",
                            required = false,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "pageable",
                            description = "페이지 정보 (기본값: size=20)",
                            required = false,
                            schema = @Schema(implementation = Pageable.class)
                    ),
            }
    )
    @GetMapping
    public ResultResponse<Page<DeliveryResponseDto>> searchDeliveries(
            @Parameter(description = "배송 검색 조건", hidden = true)
             DeliverySaerch condition,
             @ParameterObject Pageable pageable) {
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
    @Operation(summary = "배송 수정", description = "배송을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "배송 수정 성공")
    public ResultResponse<DeliveryUpdateResponseDto> updateDelivery(
            @Parameter(description = "배송 ID")
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
