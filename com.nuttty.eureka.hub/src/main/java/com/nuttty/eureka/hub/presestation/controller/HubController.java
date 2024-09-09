package com.nuttty.eureka.hub.presestation.controller;

import com.nuttty.eureka.hub.application.service.HubService;
import com.nuttty.eureka.hub.presestation.request.HubRequest;
import com.nuttty.eureka.hub.presestation.response.HubDelResponse;
import com.nuttty.eureka.hub.presestation.response.HubResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class HubController {

    private final HubService hubService;

    /**
     * 허브 생성 | 마스터 허용
     * @param request
     * @param userId
     * @param role
     * @return
     */
    // TODO required = true 변경, validateRoleMaster 주석 해제, 헤더 키 값 확인
    @PostMapping("/hubs")
    public ResponseEntity<?> createHub(@Valid @RequestBody HubRequest request,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                       @RequestHeader(value = "X-Role", required = false) String role) {
//        validateRoleMaster(role);
        log.info("허브 생성 시도 중 | request: {}, loginUser: {}", request, userId);

        HubResponse response = hubService.createHub(request);

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
    // TODO required = true 변경, validateRoleMaster 주석 해제, 헤더 키 값 확인
    public ResponseEntity<?> updateHub(@Valid @RequestBody HubRequest request,
                                       @PathVariable("hub_id") UUID hubId,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                       @RequestHeader(value = "X-Role", required = false) String role) {
//        validateRoleMaster(role);
        log.info("허브 수정 시도 중 | request: {}, loginUser: {}, hubId: {}", request, userId, hubId);

        HubResponse response = hubService.updateHub(request, hubId);

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
    // TODO required = true 변경, validateRoleMaster 주석 해제, 헤더 키 값 확인
    public ResponseEntity<?> deleteHub(@PathVariable("hub_id") UUID hubId,
                                       @RequestHeader(value = "X-Role", required = false) String role,
                                       @RequestHeader(value = "X-Email", required = false) String email) {
//        validateRoleMaster(role);
        log.info("허브 삭제 시도 중 | hubId: {}, role: {}, email: {}", hubId, role, email);

        hubService.deleteHub(hubId, email);

        log.info("허브 삭제 완료 | hubId: {}, role: {}, email: {}", hubId, role, email);
        return ResponseEntity.ok(new HubDelResponse(HttpStatus.OK.value(), "hub deleted", hubId + " 삭제 완료"));
    }

    /**
     * 마스터 권한 체크 메서드
     * @param role
     * @return
     */
    public boolean validateRoleMaster(String role) {
        if (role.equals("ROLE_MASTER")) {
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "마스터만 접근 가능합니다.");
        }
    }
}
