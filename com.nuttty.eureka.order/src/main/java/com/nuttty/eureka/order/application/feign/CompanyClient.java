package com.nuttty.eureka.order.application.feign;

import com.nuttty.eureka.order.application.feign.dto.CompanyInfoDto;
import com.nuttty.eureka.order.application.feign.dto.ProductInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {
    @GetMapping("/api/v1/companies/{id}")
    CompanyInfoDto getCompany(@PathVariable("id") UUID id,
                              @RequestHeader("X-Forwarded-Port") String port);

    // 상품 정보 조회
    @GetMapping("/api/v1/products/{id}")
    ProductInfoDto getProduct(@PathVariable("id") UUID id,
                              @RequestHeader("X-Forwarded-Port") String port);

    // 상품 수량 감소
    @PatchMapping("/api/v1/products/remove_stock/{id}")
    Integer decreaseStock(@PathVariable("id") UUID id, @RequestParam("quantity") Integer quantity,
                          @RequestHeader("X-Forwarded-Port") String port);

}
