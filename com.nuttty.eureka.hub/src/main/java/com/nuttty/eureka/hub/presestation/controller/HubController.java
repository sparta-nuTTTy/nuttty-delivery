package com.nuttty.eureka.hub.presestation.controller;

import com.nuttty.eureka.hub.application.service.HubService;
import com.nuttty.eureka.hub.presestation.request.HubRequest;
import com.nuttty.eureka.hub.presestation.response.HubResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        log.info("허브 생성 시도 중 | request: {}, userId: {}", request, userId);

        HubResponse response = hubService.createHub(request, userId);

        log.info("허브 생성 성공 | response: {}", response);
        return ResponseEntity.ok(response);
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
