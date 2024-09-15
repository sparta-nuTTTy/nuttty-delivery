package com.nuttty.eureka.ai.domain.service;

import com.nuttty.eureka.ai.application.client.DeliveryPersonClient;
import com.nuttty.eureka.ai.application.dto.deliveryperson.DeliveryPersonDto;
import com.nuttty.eureka.ai.application.dto.deliveryperson.DeliveryPersonRequestDto;
import com.nuttty.eureka.ai.exception.exceptionsdefined.ClientException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
@Transactional(readOnly = true)
public class DeliveryPersonService {

    private final DeliveryPersonClient deliveryPersonClient;

    /**
     * 공통 허브 배송 담당자 슬랙 아이디 조회
     * @return
     */
    public Map<UUID, List<String>> findAllCommonDeliveryPersonSlackId() {

        // 공통 허브 배송 담당자 조회
        DeliveryPersonRequestDto hubTransferPerson = null;
        try {
            hubTransferPerson = deliveryPersonClient.findAllCommonDeliveryPerson(20, "HUB_TRANSFER_PERSON");
        } catch (FeignException e) {
            int statusCode = e.status();
            String errorMessage = e.getMessage();
            log.error("Error occurred with status code: " + statusCode + ", message: " + e.getMessage());
            throw new ClientException(errorMessage, statusCode);

        } catch (Exception e) {
            log.error("Error occurred with exception: " + e.getMessage());
            throw new ClientException(e.getMessage());
        }

        if (hubTransferPerson == null) {
            log.error("Hub transfer person is null");
            throw new ClientException("Hub transfer person is null");
        }

        Page<DeliveryPersonDto> deliveryPersonDto = hubTransferPerson.getData();

        // 공통 허브 배송 담당자 슬랙 ID 리스트
        Map<UUID, List<String>> slackIdMap = new HashMap<>();

        for (DeliveryPersonDto personDto : deliveryPersonDto) {
            UUID deliveryPersonId = personDto.getDeliveryPersonId();

            slackIdMap.computeIfAbsent(deliveryPersonId, k -> new ArrayList<>()).add(personDto.getSlackId());
        }

        return slackIdMap;
    }
}
