package com.nuttty.eureka.auth.presentation.controller;

import com.nuttty.eureka.auth.application.dto.DeliveryPersonInfoDto;
import com.nuttty.eureka.auth.application.dto.DeliveryPersonSearchResponseDto;
import com.nuttty.eureka.auth.application.service.DeliveryPersonService;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonCreateDto;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonSearchRequestDto;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonTypeUpdateRequestDto;
import com.nuttty.eureka.auth.util.ResultResponse;
import com.nuttty.eureka.auth.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/delivery-people")
@RequiredArgsConstructor
public class DeliveryPersonController {

    private final DeliveryPersonService deliveryPersonService;

    // 배송 담당자 등록
    @PostMapping
    public ResultResponse<DeliveryPersonInfoDto> createDeliveryPerson(@RequestHeader("X-User-Role") String role,
                                                                      @RequestHeader("X-User-Id") Long userId,
                                                                      @RequestBody DeliveryPersonCreateDto createDto) {

        validateUserRole(role);

        return ResultResponse.<DeliveryPersonInfoDto>builder()
                .data(deliveryPersonService.createDeliveryPerson(role, userId, createDto))
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();
    }

    // 배송 담당자 개별 조회
    @GetMapping("/{delivery_person_id}")
    public ResultResponse<DeliveryPersonInfoDto> getDeliveryPersonInfo(@RequestHeader("X-User-Role") String role,
                                                                       @RequestHeader("X-User-Id") Long userId,
                                                                       @PathVariable("delivery_person_id") Long deliveryPersonId) {

        if (UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_COMPANY)) {
            throw new AccessDeniedException("접근 가능한 권한이 아닙니다.");
        }

        return ResultResponse.<DeliveryPersonInfoDto>builder()
                .data(deliveryPersonService.getDeliveryPersonInfo(role, userId, deliveryPersonId))
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }

    // 배송 담당자 정보 수정
    @PatchMapping("/{delivery_person_id}")
    public ResultResponse<DeliveryPersonInfoDto> updateDeliveryPersonType(@RequestHeader("X-User-Role") String role,
                                                                          @RequestHeader("X-User-Id") Long userId,
                                                                          @PathVariable("delivery_person_id") Long deliveryPersonId,
                                                                          @RequestBody DeliveryPersonTypeUpdateRequestDto updateDto) {
        validateUserRole(role);

        return ResultResponse.<DeliveryPersonInfoDto>builder()
                .data(deliveryPersonService.updateDeliveryPersonType(role, userId, deliveryPersonId, updateDto))
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
    }

    // 배송 담당자 삭제
    @DeleteMapping("/{delivery_person_id}")
    public ResultResponse<String> deleteDeliveryPerson(@RequestHeader("X-User-Role") String role,
                                                       @RequestHeader("X-User-Id") Long userId,
                                                       @RequestHeader("X-User-Email") String email,
                                                       @PathVariable("delivery_person_id") Long deliveryPersonId) {
        validateUserRole(role);

        return ResultResponse.<String>builder()
                .data(deliveryPersonService.deleteDeliveryPerson(role, userId, email, deliveryPersonId))
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();
    }

    // 배송 담당자 전체 조회
    @GetMapping("/search")
    public ResultResponse<Page<DeliveryPersonSearchResponseDto>> searchDeliveryPerson(@RequestHeader("X-User-Role") String role,
                                                                                      @PageableDefault Pageable pageable,
                                                                                      DeliveryPersonSearchRequestDto searchRequestDto) {
        validateUserRole(role);
        return ResultResponse.<Page<DeliveryPersonSearchResponseDto>>builder()
                .data(deliveryPersonService.searchDeliveryPerson(pageable, searchRequestDto))
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }


    // 권한 체크
    private void validateUserRole(String role) {
        if (!UserRoleEnum.valueOf(role).equals(UserRoleEnum.MASTER) && !UserRoleEnum.valueOf(role).equals(UserRoleEnum.HUB_MANAGER)) {
            throw new AccessDeniedException("접근 가능한 권한이 아닙니다.");
        }
    }
}
