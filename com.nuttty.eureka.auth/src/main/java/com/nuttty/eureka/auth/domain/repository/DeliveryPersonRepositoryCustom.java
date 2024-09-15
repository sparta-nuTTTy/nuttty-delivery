package com.nuttty.eureka.auth.domain.repository;

import com.nuttty.eureka.auth.application.dto.DeliveryPersonSearchResponseDto;
import com.nuttty.eureka.auth.presentation.request.DeliveryPersonSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryPersonRepositoryCustom {
    Page<DeliveryPersonSearchResponseDto> findAllDeliveryPerson(Pageable pageable, DeliveryPersonSearchRequestDto searchRequestDto);
}
