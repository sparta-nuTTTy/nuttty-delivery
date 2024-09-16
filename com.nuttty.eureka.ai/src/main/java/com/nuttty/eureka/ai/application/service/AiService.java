package com.nuttty.eureka.ai.application.service;

import com.nuttty.eureka.ai.application.dto.ai.AiDto;
import com.nuttty.eureka.ai.application.dto.ai.AiSearchRequestDto;
import com.nuttty.eureka.ai.application.dto.combine.DeliveryWithWeatherDto;
import com.nuttty.eureka.ai.application.dto.delivery.DeliveryDto;
import com.nuttty.eureka.ai.application.dto.deliveryperson.DeliveryPersonSearchDto;
import com.nuttty.eureka.ai.application.dto.hub.HubDto;
import com.nuttty.eureka.ai.domain.model.Ai;
import com.nuttty.eureka.ai.domain.service.*;
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
import java.util.*;

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
    private final DeliveryService deliveryService;
    private final HubService hubService;
    private final WeatherService weatherService;

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
    // TODO 슬랙 메세지 저장 해야함
    public void sendOrderToHubTransferPersonViaSlack() throws IOException, SlackApiException, URISyntaxException {

        // 전일 오전 8시 ~ 금일 오전 7시 59분 59초 허브 별 주문건 가져오기
        String order = hubMappingOrderService.getOrder();

        // 공통 허브 배송 담당자 리스트
        List<DeliveryPersonSearchDto> findDeliveryPerson = deliveryPersonService.findAllCommonDeliveryPersonSlackId();

        // 공통 허브 배송 담당자 slackID
        List<String> slackIdList = new ArrayList<>();

        // 공통 허브 배송 담당자 ID
        List<UUID> deliveryPersonIdList = new ArrayList<>();
        for (DeliveryPersonSearchDto deliveryPersonDto : findDeliveryPerson) {
            slackIdList.add(deliveryPersonDto.getSlackId());
            deliveryPersonIdList.add(deliveryPersonDto.getHubId());
        }

        // Gemini API 통해 허브 별 주문정보 메세지 요약 시키기
        String message = geminiApiService.callGemini(order);

        // 공통 허브 배송 담당자들에게 슬랙 메세지 보내기
        slackService.sendMessage(message, slackIdList);

        // 배송 담당자 받은 허브 별 주문 답변 DB 저장
        for (UUID uuid : deliveryPersonIdList) {
            aiRepository.save(Ai.createAi(message, uuid));
        }

    }

    /**
     * 허브 별 날씨 정보 배송 기사에게 전송
     * 전일 오전 6시 ~ 금일 오전 5시 59분 59초 배송 정보
     * @throws URISyntaxException
     * @throws SlackApiException
     * @throws IOException
     */
    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void sendWeather() throws URISyntaxException, SlackApiException, IOException {

        // 출발 허브 별 배송 정보
        log.info("출발 허브 별 배송 정보 start");
        Map<UUID, List<DeliveryDto>> allDelivery = deliveryService.findAllDelivery();
        log.info("출발 허브 별 배송 정보 end");

        // 허브 ID 리스트
        List<UUID> hubIdList = new ArrayList<>(allDelivery.keySet());

        // 허브 리스트
        log.info("허브 리스트 start");
        List<HubDto> findHubList = hubService.findByHubId(hubIdList);
        log.info("허브 리스트 end");

        // 허브 위도, 경도 별 날씨 정보
        log.info("허브 위도, 경도 별 날씨 정보 start");
        Map<UUID, String> weather = weatherService.wheather(findHubList);
        log.info("허브 위도, 경도 별 날씨 정보 end");

        // 배송 정보와 날씨 정보를 합쳐서 새로운 맵 생성
        Map<UUID, DeliveryWithWeatherDto> combinedMap = new HashMap<>();

        for (Map.Entry<UUID, List<DeliveryDto>> entry : allDelivery.entrySet()) {
            UUID hubId = entry.getKey();
            List<DeliveryDto> deliveries = entry.getValue();

            // 동일한 허브 ID의 날씨 정보가 있는 경우에만 합침
            if (weather.containsKey(hubId)) {
                String weatherInfo = weather.get(hubId);
                combinedMap.put(hubId, new DeliveryWithWeatherDto(deliveries, weatherInfo));
            }
        }

        // 업체 배송 담당자 slackId 키, 날씨 정보 value
        log.info("업체 배송 담당자 slackId 키, 날씨 정보 value start");
        Map<String, String> allDeliveryPerson = deliveryPersonService.findAllDeliveryPerson(combinedMap);
        log.info("업체 배송 담당자 slackId 키, 날씨 정보 value end");

        // 슬랙 메세지 보내기
        log.info("슬랙 메세지 보내기 start");
        slackService.sendMessageForMap(allDeliveryPerson);
        log.info("슬랙 메세지 보내기 end");
    }
}
