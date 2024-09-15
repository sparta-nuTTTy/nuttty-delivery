package com.nuttty.eureka.ai.domain.repository;

import com.nuttty.eureka.ai.application.dto.ai.AiSearchRequestDto;
import com.nuttty.eureka.ai.presentation.response.AiResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AiRepositoryCustom {
    Page<AiResponseDto> findAllAi(Pageable pageable, AiSearchRequestDto condition);
}
