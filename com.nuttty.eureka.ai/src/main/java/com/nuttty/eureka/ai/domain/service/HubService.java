package com.nuttty.eureka.ai.domain.service;

import com.nuttty.eureka.ai.application.client.HubClient;
import com.nuttty.eureka.ai.application.dto.hub.HubDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HubService {

    private final HubClient hubClient;

    public List<HubDto> findByHubId(List<UUID> hubIdList) {

        // 허브 리스트
        List<HubDto> hubDtoList = new ArrayList<>();

        for (UUID hubId : hubIdList) {
            HubDto hubDto = hubClient.findByHubId(hubId, "19092").getHubDto();
            hubDtoList.add(hubDto);
        }
        return hubDtoList;
    }

}