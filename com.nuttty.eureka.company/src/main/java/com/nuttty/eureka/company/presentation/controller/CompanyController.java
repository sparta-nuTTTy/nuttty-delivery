package com.nuttty.eureka.company.presentation.controller;

import com.nuttty.eureka.company.application.service.CompanyService;
import com.nuttty.eureka.company.presentation.request.CompanyRequestDto;
import com.nuttty.eureka.company.presentation.request.CompanySearchRequestDto;
import com.nuttty.eureka.company.presentation.response.CompanyDelResponseDto;
import com.nuttty.eureka.company.presentation.response.CompanyResponseDto;
import com.nuttty.eureka.company.presentation.response.CompanySearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 업체 생성 | 마스터, 허브 관리자, 허브 업체 허용
     * @param request
     * @param role
     * @param userId
     * @return
     */
    @PostMapping("/companies")
    public ResponseEntity<?> createCompany(@RequestBody CompanyRequestDto request,
                                           @RequestHeader(value = "X-User-Role") String role,
                                           @RequestHeader(value = "X-User-Id") Long userId) {
        validateExcludeRoleDeliveryPerson(role);
        log.info("업체 생산 시도 중 | request: {}, userId: {} | role: {}", request, userId, role);

        CompanyResponseDto response = companyService.createCompany(request, userId, role);
        log.info("업체 생산 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 업체 수정 | 마스터, 허브 관리자, 허브 업체(본인만) 허용
     * @param companyId
     * @param request
     * @param role
     * @param userId
     * @return
     */
    @PatchMapping("/companies/{company_id}")
    public ResponseEntity<?> updateCompany(@PathVariable("company_id") UUID companyId,
                                           @RequestBody CompanyRequestDto request,
                                           @RequestHeader(value = "X-User-Role") String role,
                                           @RequestHeader(value = "X-User-Id") Long userId) {
        validateExcludeRoleDeliveryPerson(role);
        log.info("업체 수정 시도 중 | request: {}, userId: {} | company_id: {}", request, userId, companyId);

        CompanyResponseDto response = companyService.updateCompany(request, companyId, userId, role);
        log.info("업체 수정 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 업체 삭제 | 마스터 허용
     * @param companyId
     * @param role
     * @param email
     * @return
     */
    @DeleteMapping("/companies/{company_id}")
    public ResponseEntity<?> deleteCompany(@PathVariable("company_id") UUID companyId,
                                           @RequestHeader(value = "X-User-Role") String role,
                                           @RequestHeader(value = "X-User-Email") String email) {
        validateRoleMaster(role);
        log.info("업체 삭제 시도 중 | company_id: {}, email: {}", companyId, email);

        CompanyDelResponseDto response = companyService.deleteCompany(companyId, email);
        log.info("업체 삭제 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 업체 개별 조회 | 모두 허용
     * @param companyId
     * @return
     */
    @GetMapping("/companies/{company_id}")
    public ResponseEntity<?> findOneCompany(@PathVariable("company_id") UUID companyId) {
        log.info("업체 개별 조회 시도 중 | company_id: {}", companyId);

        CompanyResponseDto response = companyService.findOneCompany(companyId);
        log.info("업체 개별 조회 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 업체 페이지 조회 | 모두 허용
     * @param pageable
     * @param condition
     * @return
     */
    @GetMapping("/companies")
    public ResponseEntity<?> findAllCompany(Pageable pageable, CompanySearchRequestDto condition) {
        log.info("업체 페이지 조회 시도 중 | condition: {}, pageble: {}", condition, pageable);

        Page<CompanySearchResponseDto> response = companyService.findAllCompany(pageable, condition);
        log.info("업체 페이지 조회 완료");
        return ResponseEntity.ok(response);
    }

    /**
     * 마스터 권한 확인
     * @param role
     * @return
     */
    private boolean validateRoleMaster(String role) {
        if (!role.equals("MASTER")) {
            log.error("마스터만 접근 가능합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "마스터만 접근 가능합니다.");
        }else {
            return true;
        }
    }

    /**
     * 배송 담당자 제외 검증
     * @param role
     * @return
     */
    private boolean validateExcludeRoleDeliveryPerson(String role) {
        if (role.equals("HUB_DELIVERY_PERSON")) {
            log.error("배송 담당자는 접근 불가능 합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "배송 담당자는 접근 불가능합니다.");
        }else {
            return true;
        }
    }
    
    
}
