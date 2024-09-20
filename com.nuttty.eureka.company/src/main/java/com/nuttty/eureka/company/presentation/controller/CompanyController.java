package com.nuttty.eureka.company.presentation.controller;

import com.nuttty.eureka.company.application.dto.CompanyDto;
import com.nuttty.eureka.company.application.dto.UserRoleEnum;
import com.nuttty.eureka.company.application.security.UserDetailsImpl;
import com.nuttty.eureka.company.application.service.CompanyService;
import com.nuttty.eureka.company.presentation.request.CompanyRequestDto;
import com.nuttty.eureka.company.presentation.request.CompanySearchRequestDto;
import com.nuttty.eureka.company.presentation.response.CompanyDelResponseDto;
import com.nuttty.eureka.company.presentation.response.CompanyResponseDto;
import com.nuttty.eureka.company.presentation.response.CompanySearchResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "업체", description = "업체 등록, 조회, 수정, 삭제, 재고 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 업체 생성 | 마스터, 허브 관리자, 허브 업체 허용
     * @param request
     * @return
     */
    @Operation(summary = "업체 생성", description = "업체 생성 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "업체가 생성되었습니다."),
            @ApiResponse(responseCode = "403", description = "업체 생성 권한이 없습니다."),
    })
    @PostMapping("/companies")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER', 'HUB_COMPANY')")
    public ResponseEntity<?> createCompany(@Valid @RequestBody CompanyRequestDto request,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UserRoleEnum role = userDetails.getUser().getRole();
        Long userId = userDetails.getUserId();

        log.info("업체 생산 시도 중 | request: {}, userId: {} | role: {}", request, userId, role);

        CompanyResponseDto response = companyService.createCompany(request, userId, role);
        log.info("업체 생산 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 업체 수정 | 마스터, 허브 관리자, 허브 업체(본인만) 허용
     * @param companyId
     * @param request
     * @param userDetails
     * @return
     */
    @Operation(summary = "업체 수정", description = "업체 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업체가 수정되었습니다."),
            @ApiResponse(responseCode = "403", description = "업체 수정 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "업체가 존재하지 않습니다.")
    })
    @PatchMapping("/companies/{company_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER', 'HUB_COMPANY')")
    public ResponseEntity<?> updateCompany(@Parameter(description = "수정할 업체 ID") @PathVariable("company_id") UUID companyId,
                                           @Valid @RequestBody CompanyRequestDto request,

                                           @Parameter(hidden = true)
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String role = userDetails.getUser().getRole().getAuthority();
        Long userId = userDetails.getUserId();
        log.info("업체 수정 시도 중 | request: {}, userId: {} | company_id: {}", request, userId, companyId);

        CompanyResponseDto response = companyService.updateCompany(request, companyId, userId, role);
        log.info("업체 수정 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 업체 삭제 | 마스터 허용
     * @param companyId
     * @param userDetails
     * @return
     */
    @Operation(summary = "업체 삭제", description = "업체를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업체가 삭제되었습니다."),
            @ApiResponse(responseCode = "403", description = "업체 삭제 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "업체가 존재하지 않습니다.")
    })
    @DeleteMapping("/companies/{company_id}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<?> deleteCompany(@Parameter(description = "삭제할 업체의 ID") @PathVariable("company_id") UUID companyId,

                                           @Parameter(hidden = true)
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String email = userDetails.getUser().getEmail();

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
    @Operation(summary = "업체 단건 조회", description = "업체 한 건을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업체 조회 성공"),
            @ApiResponse(responseCode = "404", description = "업체가 존재하지 않습니다.")
    })
    @GetMapping("/companies/{company_id}")
    public ResponseEntity<?> findOneCompany(@Parameter(description = "조회할 업체의 ID") @PathVariable("company_id") UUID companyId) {
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
    @Operation(
            summary = "허브 전체 조회",
            description = "모든 허브를 페이징하여 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "company_id",
                            description = "업체의 고유 식별자 (UUID)",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "user_id",
                            description = "사용자의 고유 ID",
                            schema = @Schema(type = "integer", format = "int64")
                    ),
                    @Parameter(
                            name = "hub_id",
                            description = "허브의 고유 식별자 (UUID)",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "name",
                            description = "업체의 주소",
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "type",
                            description = "업체의 종류",
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "address",
                            description = "업체의 주소",
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업체 전체 조회 성공")
    })
    @GetMapping("/companies")
    public ResponseEntity<?> findAllCompany(@ParameterObject @PageableDefault(size = 10) Pageable pageable,
                                            @Parameter(hidden = true) CompanySearchRequestDto condition) {
        log.info("업체 페이지 조회 시도 중 | condition: {}, pageble: {}", condition, pageable);

        Page<CompanySearchResponseDto> response = companyService.findAllCompany(pageable, condition);
        log.info("업체 페이지 조회 완료");
        return ResponseEntity.ok(response);
    }

    /**
     * 공급업체 ID = CompanyID, 공급 업체 ID 로 업체 리스트 조회
     * @param supplierId
     * @return
     */
    @Operation(summary = "공급 업체 ID 리스트로 업체 리스트 조회", description = "업체 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업체 조회 성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식이 틀렸습니다."),

    })
    @GetMapping("/companies/list")
    public ResponseEntity<?> findAllSupplierIdOfCompany(@Parameter(description = "업체 ID 리스트를 작성해주세요.", schema = @Schema(type = "array", format = "uuid"))
                                                            @RequestParam("supplierId") List<UUID> supplierId) {
        log.info("supplierId 로 업체 전체 조회 시도 중 | supplierId: {}", supplierId);

        List<CompanyDto> response = companyService.findAllSupplierIdOfCompany(supplierId);
        log.info("supplierId 로 업체 전체 조회 완료");
        return ResponseEntity.ok(response);
    }
}
