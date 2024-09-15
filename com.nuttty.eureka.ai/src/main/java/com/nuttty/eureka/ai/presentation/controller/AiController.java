package com.nuttty.eureka.ai.presentation.controller;

import com.nuttty.eureka.ai.application.dto.ai.AiSearchRequestDto;
import com.nuttty.eureka.ai.application.service.AiService;
import com.nuttty.eureka.ai.presentation.response.AiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * Ai 단 건 조회 | 마스터, 허브관리자, 배송 담당자
     * @param aiId
     * @return
     */
    @GetMapping("/ai/{ai_id}")
    public ResponseEntity<?> findOneAi(@PathVariable("ai_id") UUID aiId,
                                       @RequestHeader("X-User-Role") String role) {
        validateExcludeRoleHubCompany(role);
        log.info("ai 조회 시도 중 | ai_id: {}", aiId);

        AiResponseDto response = aiService.findOneAi(aiId);

        log.info("ai 조회 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * ai 페이징 전체 조회 | 마스터, 허브관리자, 배송 담당자
     * @param role
     * @param condition
     * @param pageable
     * @return
     */
    @GetMapping("/ai")
    public ResponseEntity<?> findAllAi(@RequestHeader("X-User-Role") String role,
                                       AiSearchRequestDto condition,
                                       Pageable pageable) {
        validateExcludeRoleHubCompany(role);
        log.info("ai 전체 조회 시도 중 | condition: {}, pageable: {}", condition, pageable);

        Page<AiResponseDto> response = aiService.findAllAi(pageable, condition);

        log.info("ai 전체 조회 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * ai 삭제 | 마스터 허용
     * @param aiId
     * @param role
     * @param email
     * @return
     */
    @DeleteMapping("/ai/{ai_id}")
    public ResponseEntity<?> deleteAi(@PathVariable("ai_id") UUID aiId,
                                      @RequestHeader("X-User-Role") String role,
                                      @RequestHeader("X-User-Email") String email) {
        validateMaster(role);
        log.info("ai 삭제 시도 중 | ai_id: {}", aiId);

        AiResponseDto response = aiService.deleteAi(aiId, email);

        log.info("ai 삭제 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }


    /**
     * 마스터 검증
     * @param role
     * @return
     */
    private boolean validateMaster(String role) {
        if (role.equals("MASTER")) {
            return true;
        }else {
            log.error("마스터만 접근 가능합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "마스터만 접근 가능합니다.");
        }
    }

    /**
     * 허브 업체 제외 검증
     * @param role
     * @return
     */
    private boolean validateExcludeRoleHubCompany(String role) {
        if (role.equals("HUB_COMPANY")) {
            log.error("허브 업체는 접근 불가능 합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "허브 업체는 접근 불가능 합니다.");
        }else {
            return true;
        }
    }
}