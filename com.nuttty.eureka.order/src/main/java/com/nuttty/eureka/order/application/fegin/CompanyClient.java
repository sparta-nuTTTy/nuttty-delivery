package com.nuttty.eureka.order.application.fegin;

import com.nuttty.eureka.order.application.fegin.dto.CompanyInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {
    @GetMapping("/api/v1/companies/{id}")
    CompanyInfoDto getCompany(@PathVariable("id") UUID id);

}
