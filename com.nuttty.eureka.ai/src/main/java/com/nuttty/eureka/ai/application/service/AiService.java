package com.nuttty.eureka.ai.application.service;

import com.nuttty.eureka.ai.application.dto.ai.AiDto;
import com.nuttty.eureka.ai.application.dto.ai.AiSearchRequestDto;
import com.nuttty.eureka.ai.domain.model.Ai;
import com.nuttty.eureka.ai.domain.service.DeliveryPersonService;
import com.nuttty.eureka.ai.domain.service.GeminiApiService;
import com.nuttty.eureka.ai.domain.service.HubMappingOrderService;
import com.nuttty.eureka.ai.domain.service.SlackService;
import com.nuttty.eureka.ai.infrastructure.repository.AiRepository;
import com.nuttty.eureka.ai.presentation.response.AiResponseDto;
import com.slack.api.methods.SlackApiException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiService {

    private final AiRepository aiRepository;
    private final HubMappingOrderService hubMappingOrderService;
    private final DeliveryPersonService deliveryPersonService;
    private final GeminiApiService geminiApiService;
    private final SlackService slackService;

    /**
     * Ai 단 건 조회 | 마스터, 허브관리자, 배송 담당자
     * @param aiId
     * @return
     */
    public AiResponseDto findOneAi(UUID aiId) {

        Ai ai = aiRepository.findById(aiId).orElseThrow(() ->
                new EntityNotFoundException("not found ai with id: " + aiId));

        return new AiResponseDto(HttpStatus.OK.value(), "ai found", AiDto.toDto(ai));
    }

    /**
     * ai 페이징 전체 조회 | 마스터, 허브관리자, 배송 담당자
     * @param pageable
     * @param condition
     * @return
     */
    public Page<AiResponseDto> findAllAi(Pageable pageable, AiSearchRequestDto condition) {
        return aiRepository.findAllAi(pageable, condition);
    }

    /**
     * ai 삭제 | 마스터 허용
     * @param aiId
     * @param email
     * @return
     */
    @Transactional
    public AiResponseDto deleteAi(UUID aiId, String email) {

        Ai ai = aiRepository.findById(aiId).orElseThrow(() ->
                new EntityNotFoundException("not found ai with id: " + aiId));

        ai.delete(email);
        return new AiResponseDto(HttpStatus.OK.value(), "ai deleted", aiId + " 삭제 완료");
    }

    /**
     * 공통 허브 배송 담당자 10명에게 허브별 주문 정보 슬랙 메세지 보내기
     * 전일 오전 8시 ~ 금일 오전 7시 59분 59초 허브 별 주문건
     * @throws IOException
     * @throws SlackApiException
     * @throws URISyntaxException
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    // TODO 잼민이 처리시간 길어질 경우 Read Time OUT 인가 터짐. 안쓰는게 나을거 같기도 함. 나중에 물어볼 것
    public void sendOrderToHubTransferPersonViaSlack() throws IOException, SlackApiException, URISyntaxException {

        // 전일 오전 8시 ~ 금일 오전 7시 59분 59초 허브 별 주문건 가져오기
        String order = hubMappingOrderService.getOrder();

        // 공통 허브 배송 담당자 슬랙 ID 리스트
        Map<UUID, List<String>> deliveryPersonSlackId = deliveryPersonService.findAllCommonDeliveryPersonSlackId();

        // Gemini API 통해 허브 별 주문정보 메세지 요약 시키기
        String message = geminiApiService.callGemini(order);

        // 공통 허브 배송 담당자들에게 슬랙 메세지 보내기
        List<String> slackId = deliveryPersonSlackId.values().stream()
                .flatMap(List::stream)
                .toList();
        slackService.sendMessage(message, slackId);

        // 배송 담당자 받은 허브 별 주문 답변 DB 저장
        for (UUID uuid : deliveryPersonSlackId.keySet()) {
            aiRepository.save(Ai.createAi(message, uuid));
        }

    }
}
