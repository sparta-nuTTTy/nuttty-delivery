package com.nuttty.eureka.ai.application.client;

import com.nuttty.eureka.ai.application.dto.company.CompanyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/v1/companies/list")
    List<CompanyDto> findAllSupplierId(@RequestParam("supplierId") List<UUID> supplierId);



}