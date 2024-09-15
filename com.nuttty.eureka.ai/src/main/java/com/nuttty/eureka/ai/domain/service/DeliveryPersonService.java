package com.nuttty.eureka.ai.domain.service;

import com.nuttty.eureka.ai.application.client.DeliveryPersonClient;
import com.nuttty.eureka.ai.application.dto.deliveryperson.DeliveryPersonDto;
import com.nuttty.eureka.ai.application.dto.deliveryperson.DeliveryPersonRequestDto;
import com.nuttty.eureka.ai.application.dto.deliveryperson.DeliveryPersonSearchDto;
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
     * 공통 허브 배송 담당자 전체 조회
     * @return
     */
    @Transactional
    public List<DeliveryPersonSearchDto> findAllCommonDeliveryPersonSlackId() {

        // 공통 허브 배송 담당자 조회
        DeliveryPersonRequestDto hubTransferPerson = null;
        try {
            hubTransferPerson = deliveryPersonClient.findAllCommonDeliveryPerson(20, "HUB_TRANSFER_PERSON", "MASTER");
        } catch (FeignException e) {
            int statusCode = e.status();
            String errorMessage = e.getMessage();
            log.error("Error occurred with status code: " + statusCode + ", message: " + e);
            throw new ClientException(errorMessage, statusCode);

        } catch (Exception e) {
            log.error("Error occurred with exception: " + e);
            throw new ClientException(e.getMessage());
        }

        if (hubTransferPerson == null) {
            log.error("Hub transfer person is null");
            throw new ClientException("Hub transfer person is null");
        }

        // 공통 허브 배송 담당자 객체 꺼내기
        Page<DeliveryPersonDto> deliveryPersonDto = hubTransferPerson.getData();
        List<DeliveryPersonDto> content = deliveryPersonDto.getContent();
        List<DeliveryPersonSearchDto> result = new ArrayList<>();

        for (DeliveryPersonDto personDto : content) {
            result = personDto.getDeliveryPersonInfoList();
        }

        return result;
    }
}
