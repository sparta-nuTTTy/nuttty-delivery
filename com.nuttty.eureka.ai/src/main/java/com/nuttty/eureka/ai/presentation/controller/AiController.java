package com.nuttty.eureka.ai.presentation.controller;

import com.nuttty.eureka.ai.application.dto.ai.AiSearchRequestDto;
import com.nuttty.eureka.ai.application.security.UserDetailsImpl;
import com.nuttty.eureka.ai.application.service.AiService;
import com.nuttty.eureka.ai.presentation.response.AiResponseDto;
import com.slack.api.methods.SlackApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

@Tag(name = " AI", description = "AI 조회, 삭제 API")
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RefreshScope
public class AiController {

    private final AiService aiService;

    /**
     * Ai 단 건 조회 | 마스터, 허브관리자, 배송 담당자
     *
     * @param aiId
     * @return
     */
    @Operation(summary = "Ai 단건 조회", description = "Ai 한 건을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ai 조회 성공"),
            @ApiResponse(responseCode = "404", description = "Ai가 존재하지 않습니다.")
    })
    @GetMapping("/ai/{ai_id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER', 'HUB_DELIVERY_PERSON')")
    public ResponseEntity<?> findOneAi(@Parameter(description = "조회할 Ai ID") @PathVariable("ai_id") UUID aiId) {

        log.info("ai 조회 시도 중 | ai_id: {}", aiId);

        AiResponseDto response = aiService.findOneAi(aiId);

        log.info("ai 조회 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * ai 페이징 전체 조회 | 마스터, 허브관리자, 배송 담당자
     * @param condition
     * @param pageable
     * @return
     */
    @Operation(
            summary = "AI 전체 조회",
            description = "AI 상품을 페이징하여 조회합니다.",
            parameters = {
                    @Parameter(
                            name = "aiId",
                            description = "AI의 고유 식별자 (UUID)",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "deliveryManagerId",
                            description = "배송 담당자의 고유 식별자 (UUID)",
                            schema = @Schema(type = "string", format = "uuid")
                    ),
                    @Parameter(
                            name = "question",
                            description = "AI 질문 내용",
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            name = "answer",
                            description = "AI 답변 내용",
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI 전체 조회 성공")
    })
    @GetMapping("/ai")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER', 'HUB_DELIVERY_PERSON')")
    public ResponseEntity<?> findAllAi(@Parameter(hidden = true) AiSearchRequestDto condition,
                                       @ParameterObject Pageable pageable) {
        log.info("ai 전체 조회 시도 중 | condition: {}, pageable: {}", condition, pageable);

        Page<AiResponseDto> response = aiService.findAllAi(pageable, condition);

        log.info("ai 전체 조회 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     *  ai 삭제 | 마스터 허용
     * @param aiId
     * @param userDetails
     * @return
     */
    @Operation(summary = "Ai 삭제", description = "상품를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ai 삭제되었습니다."),
            @ApiResponse(responseCode = "403", description = "Ai 삭제 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "Ai 존재하지 않습니다.")
    })

    @DeleteMapping("/ai/{ai_id}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<?> deleteAi(@Parameter(description = "삭제할 Ai ID") @PathVariable("ai_id") UUID aiId,

                                      @Parameter(hidden = true)
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String email = userDetails.getUser().getEmail();

        log.info("ai 삭제 시도 중 | ai_id: {}", aiId);

        AiResponseDto response = aiService.deleteAi(aiId, email);

        log.info("ai 삭제 완료 | response: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ai/weather")
    public void weather() throws SlackApiException, URISyntaxException, IOException {
        aiService.sendWeather();
    }
}