package com.nuttty.eureka.ai.domain.service;

import com.nuttty.eureka.ai.application.client.OrderClient;
import com.nuttty.eureka.ai.application.dto.delivery.DeliveryDto;
import com.nuttty.eureka.ai.application.dto.delivery.DeliveryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryService {

    private final OrderClient orderClient;

    /**
     * 출발 허브 별 배송 정보
     * @return
     */
    public Map<UUID, List<DeliveryDto>> findAllDelivery() {

        // 전일 오전 6시 ~ 금일 오전 5시 59분 59초
        LocalDateTime startDate = LocalDateTime.now().minusDays(1).withHour(6).withMinute(0).withSecond(0);
        LocalDateTime endDate = LocalDateTime.now().withHour(5).withMinute(59).withSecond(59);

        // // 전일 오전 6시 ~ 금일 오전 5시 59분 59초 배송 정보 10만건 조회
        DeliveryRequestDto findAllDelivery = orderClient.findAllDelivery(startDate, endDate, 100000, "19092");
        List<DeliveryDto> deliveryDtos = findAllDelivery.getData().getContent();
        // 출발 허브 별 배송 정보 매핑
        Map<UUID, List<DeliveryDto>> firstHub = new HashMap<>();

        for (int i = 0; i < deliveryDtos.size(); i++) {
            UUID fistHubId = deliveryDtos.get(i).getDepartureHubId();
            firstHub.computeIfAbsent(fistHubId, k -> new ArrayList<>()).add(deliveryDtos.get(i));
        }

        return firstHub;
    }
}
