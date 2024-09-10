package com.nuttty.eureka.company.domain.repository;

import com.nuttty.eureka.company.presentation.request.CompanySearchRequestDto;
import com.nuttty.eureka.company.presentation.response.CompanySearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyRepositoryCustom {
    Page<CompanySearchResponseDto> findAllCompany(Pageable pageable, CompanySearchRequestDto condition);
}
