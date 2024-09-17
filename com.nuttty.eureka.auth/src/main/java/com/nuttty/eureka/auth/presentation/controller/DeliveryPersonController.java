package com.nuttty.eureka.auth.presentation.controller;

import com.nuttty.eureka.auth.application.dto.DeliveryPersonInfoDto;
import com.nuttty.eureka.auth.application.dto.DeliveryPersonSearchResponseDto;
import com.nuttty.eureka.auth.application.security.UserDetailsImpl;
import com.nuttty.eureka.auth.application.service.DeliveryPersonService;
import com.nuttty.eureka.auth.domain.model.UserRoleEnum;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonCreateDto;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonSearchRequestDto;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonTypeUpdateRequestDto;
import com.nuttty.eureka.auth.util.ResultResponse;
import com.nuttty.eureka.auth.util.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "DeliveryPersonController")
@RestController
@RequestMapping("/api/v1/delivery-people")
@RequiredArgsConstructor
public class DeliveryPersonController {

    private final DeliveryPersonService deliveryPersonService;

    // 배송 담당자 등록
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ResultResponse<DeliveryPersonInfoDto> createDeliveryPerson(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @RequestBody DeliveryPersonCreateDto createDto) {

        UserRoleEnum loggedUserRole = userDetails.getUser().getRole();
        Long userId = userDetails.getUserId();

        return ResultResponse.<DeliveryPersonInfoDto>builder()
                .data(deliveryPersonService.createDeliveryPerson(loggedUserRole, userId, createDto))
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();
    }

    // 배송 담당자 개별 조회
    @GetMapping("/{delivery_person_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER', 'HUB_DELIVERY_PERSON')")
    public ResultResponse<DeliveryPersonInfoDto> getDeliveryPersonInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @PathVariable("delivery_person_id") Long deliveryPersonId) {

        UserRoleEnum loggedUserRole = userDetails.getUser().getRole();
        Long userId = userDetails.getUserId();

        return ResultResponse.<DeliveryPersonInfoDto>builder()
                .data(deliveryPersonService.getDeliveryPersonInfo(loggedUserRole, userId, deliveryPersonId))
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }

    // 배송 담당자 정보 수정
    @PatchMapping("/{delivery_person_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ResultResponse<DeliveryPersonInfoDto> updateDeliveryPersonType(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                          @PathVariable("delivery_person_id") Long deliveryPersonId,
                                                                          @RequestBody DeliveryPersonTypeUpdateRequestDto updateDto) {

        UserRoleEnum loggedUserRole = userDetails.getUser().getRole();
        Long loggedUserId = userDetails.getUserId();

        return ResultResponse.<DeliveryPersonInfoDto>builder()
                .data(deliveryPersonService.updateDeliveryPersonType(loggedUserRole, loggedUserId, deliveryPersonId, updateDto))
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
    }

    // 배송 담당자 삭제
    @DeleteMapping("/{delivery_person_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ResultResponse<String> deleteDeliveryPerson(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @PathVariable("delivery_person_id") Long deliveryPersonId) {

        UserRoleEnum loggedUserRole = userDetails.getUser().getRole();
        Long loggedUserId = userDetails.getUserId();
        String email = userDetails.getUser().getEmail();

        return ResultResponse.<String>builder()
                .data(deliveryPersonService.deleteDeliveryPerson(loggedUserRole, loggedUserId, email, deliveryPersonId))
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMessage(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();
    }

    // 배송 담당자 전체 조회
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ResultResponse<Page<DeliveryPersonSearchResponseDto>> searchDeliveryPerson(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                      @PageableDefault Pageable pageable,
                                                                                      DeliveryPersonSearchRequestDto searchRequestDto) {
        UserRoleEnum loggedUserRole = userDetails.getUser().getRole();

        return ResultResponse.<Page<DeliveryPersonSearchResponseDto>>builder()
                .data(deliveryPersonService.searchDeliveryPerson(loggedUserRole, pageable, searchRequestDto))
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMessage(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
    }
}
