package com.nuttty.eureka.hub.domain.repository;

import com.nuttty.eureka.hub.presestation.request.HubSearchRequestDto;
import com.nuttty.eureka.hub.presestation.response.HubSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRepositoryCustom {
    Page<HubSearchResponseDto> findAllHub(Pageable pageable, HubSearchRequestDto condition);
}
