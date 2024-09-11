package com.nuttty.eureka.company.domain.repository;

import com.nuttty.eureka.company.presentation.request.ProductSearchRequestDto;
import com.nuttty.eureka.company.presentation.response.ProductSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductSearchResponseDto> findAllProduct(Pageable pageable, ProductSearchRequestDto condition);
}
