package com.nuttty.eureka.hub.presestation.controller;

import com.nuttty.eureka.hub.application.service.HubService;
import com.nuttty.eureka.hub.presestation.request.HubRequestDto;
import com.nuttty.eureka.hub.presestation.request.HubSearchRequestDto;
import com.nuttty.eureka.hub.presestation.response.HubDelResponseDto;
import com.nuttty.eureka.hub.presestation.response.HubResponseDto;
import com.nuttty.eureka.hub.presestation.response.HubSearchResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Tag(name = "허브", description = "허브 등록, 조회, 수정, 삭제 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
@RefreshScope
public class HubController {

    private final HubService hubService;

    /**
     * 허브 생성 | 마스터 허용
     * @param request
     * @param userId
     * @param role
     * @return
     */
    @PostMapping("/hubs")
    @Operation(summary = "허브 생성", description = "허브 생성 합니다.")
    @ApiResponse(responseCode = "201", description = "hub created")
    public ResponseEntity<?> createHub(@Valid @RequestBody HubRequestDto request,
                                       @RequestHeader(value = "X-User-Id") Long userId,
                                       @RequestHeader(value = "X-User-Role") String role) {
        validateRoleMaster(role);
        log.info("허브 생성 시도 중 | request: {}, loginUser: {}", request, userId);

        HubResponseDto response = hubService.createHub(request, userId);

        log.info("허브 생성 성공 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 허브 수정 | 마스터 허용
     * @param request
     * @param hubId
     * @param userId
     * @param role
     * @return
     */
    @PatchMapping("/hubs/{hub_id}")
    public ResponseEntity<?> updateHub(@Valid @RequestBody HubRequestDto request,
                                       @PathVariable("hub_id") UUID hubId,
                                       @RequestHeader(value = "X-User-Id") Long userId,
                                       @RequestHeader(value = "X-User-Role") String role) {
        validateRoleMaster(role);
        log.info("허브 수정 시도 중 | request: {}, loginUser: {}, hubId: {}", request, userId, hubId);

        HubResponseDto response = hubService.updateHub(request, hubId, userId);

        log.info("허브 수정 성공 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 허브 삭제 | 마스터 허용
     * @param hubId
     * @param role
     * @param email
     * @return
     */
    @DeleteMapping("/hubs/{hub_id}")
    public ResponseEntity<?> deleteHub(@PathVariable("hub_id") UUID hubId,
                                       @RequestHeader(value = "X-User-Role") String role,
                                       @RequestHeader(value = "X-User-Email") String email) {
        validateRoleMaster(role);
        log.info("허브 삭제 시도 중 | hubId: {}, role: {}, email: {}", hubId, role, email);

        hubService.deleteHub(hubId, email);

        log.info("허브 삭제 완료 | hubId: {}, role: {}, email: {}", hubId, role, email);
        return ResponseEntity.ok(new HubDelResponseDto(HttpStatus.OK.value(), "hub deleted", hubId + " 삭제 완료"));
    }

    /**
     * 허브 단건 조회 | 모두 허용
     * @param hubId
     * @return
     */
    @GetMapping("/hubs/{hub_id}")
    public ResponseEntity<?> findOneHub(@PathVariable("hub_id") UUID hubId) {
        log.info("허브 단건 조회 시도 중 | hubId: {}", hubId);

        HubResponseDto response = hubService.findOneHub(hubId);

        log.info("허브 단건 조회 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 허브 페이징 조회 | 모두 허용
     * @param condition
     * @param pageable
     * @return
     */
    @GetMapping("/hubs")
    public ResponseEntity<?> findAllHub(HubSearchRequestDto condition,
                                        @PageableDefault(size = 20) Pageable pageable) {
        log.info("허브 전체 조회 시도 중 | condition: {}, pageble: {}", condition, pageable);

        Page<HubSearchResponseDto> findAllHub = hubService.findAllHub(pageable, condition);

        log.info("허브 전체 조회 완료 | condition: {}, pageble: {}", condition, pageable);
        return ResponseEntity.ok(findAllHub);
    }

    /**
     * 마스터 권한 체크 메서드
     * @param role
     * @return
     */
    public boolean validateRoleMaster(String role) {
        if (role.equals("MASTER")) {
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "마스터만 접근 가능합니다.");
        }
    }
}
