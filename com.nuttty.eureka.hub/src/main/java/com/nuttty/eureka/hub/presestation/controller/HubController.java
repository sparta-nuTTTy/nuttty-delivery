package com.nuttty.eureka.hub.presestation.controller;

import com.nuttty.eureka.hub.application.security.UserDetailsImpl;
import com.nuttty.eureka.hub.application.service.HubService;
import com.nuttty.eureka.hub.presestation.request.HubRequestDto;
import com.nuttty.eureka.hub.presestation.request.HubSearchRequestDto;
import com.nuttty.eureka.hub.presestation.response.HubDelResponseDto;
import com.nuttty.eureka.hub.presestation.response.HubResponseDto;
import com.nuttty.eureka.hub.presestation.response.HubSearchResponseDto;
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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
     * @return
     */
    @PostMapping("/hubs")
    @Operation(summary = "허브 생성", description = "허브 생성 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "허브가 생성되었습니다."),
            @ApiResponse(responseCode = "403", description = "허브 생성 권한이 없습니다."),
    })
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<?> createHub(@Valid @RequestBody HubRequestDto request) {
        log.info("허브 생성 시도 중 | request: {}", request);

        HubResponseDto response = hubService.createHub(request);

        log.info("허브 생성 성공 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 허브 수정 | 마스터 허용
     * @param request
     * @param hubId
     * @param userDetails
     * @return
     */
    @PatchMapping("/hubs/{hub_id}")
    @Operation(summary = "허브 수정", description = "허브 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "허브가 수정되었습니다."),
            @ApiResponse(responseCode = "403", description = "허브 수정 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "허브가 존재하지 않습니다.")
    })
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<?> updateHub(@Valid @RequestBody HubRequestDto request,

                                       @Parameter(description = "수정할 허브의 ID")
                                       @PathVariable("hub_id") UUID hubId,

                                       @Parameter(hidden = true)
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();

        log.info("허브 수정 시도 중 | request: {}, loginUser: {}, hubId: {}", request, userId, hubId);

        HubResponseDto response = hubService.updateHub(request, hubId, userId);

        log.info("허브 수정 성공 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * 허브 삭제 | 마스터 허용
     * @param hubId
     * @param userDetails
     * @return
     */
    @DeleteMapping("/hubs/{hub_id}")
    @Operation(summary = "허브 삭제", description = "허브를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "허브가 삭제되었습니다."),
            @ApiResponse(responseCode = "403", description = "허브 삭제 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "허브가 존재하지 않습니다.")
    })
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<?> deleteHub(@Parameter(description = "삭제할 허브의 ID") @PathVariable("hub_id") UUID hubId,

                                       @Parameter(hidden = true)
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String role = userDetails.getUser().getRole().getAuthority();
        String email = userDetails.getUser().getEmail();

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
    @Operation(summary = "허브 단건 조회", description = "허브 한 건을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "허브 조회 성공"),
            @ApiResponse(responseCode = "404", description = "허브가 존재하지 않습니다.")
    })
    public ResponseEntity<?> findOneHub(@Parameter(description = "조회할 허브의 ID") @PathVariable("hub_id") UUID hubId) {
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
    @Operation(
            summary = "허브 전체 조회",
            description = "모든 허브를 페이징하여 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "hub_id",
                            description = "허브의 고유 식별자 (UUID)",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "user_id",
                            description = "사용자의 고유 ID",
                            schema = @Schema(type = "integer", format = "int64")
                    ),
                    @Parameter(
                            name = "name",
                            description = "허브의 이름",
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "address",
                            description = "허브의 주소",
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "허브 전체 조회 성공")
    })
    public ResponseEntity<?> findAllHub(@Parameter(hidden = true) HubSearchRequestDto condition,
                                        @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        log.info("허브 전체 조회 시도 중 | condition: {}, pageble: {}", condition, pageable);

        Page<HubSearchResponseDto> findAllHub = hubService.findAllHub(pageable, condition);

        log.info("허브 전체 조회 완료 | condition: {}, pageble: {}", condition, pageable);
        return ResponseEntity.ok(findAllHub);
    }
}
