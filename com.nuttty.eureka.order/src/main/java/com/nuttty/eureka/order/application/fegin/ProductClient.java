package com.nuttty.eureka.order.application.fegin;

import com.nuttty.eureka.order.application.fegin.dto.ProductInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {
    // 상품 정보 조회
    @GetMapping("/api/v1/products/{id}")
    ProductInfoDto getProduct(@PathVariable("id") UUID id);

    // 상품 수량 감소
    @PatchMapping("/api/v1/products/remove_stock/{id}")
    Integer decreaseStock(@PathVariable("id") UUID id, @RequestParam("quantity") Integer quantity);

}
