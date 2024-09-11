package com.nuttty.eureka.order.infrastructure.fegin;

import com.nuttty.eureka.order.infrastructure.fegin.dto.ProductInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {
    // 상품 정보 조회
    @GetMapping("/api/v1/products/{id}")
    ProductInfoDto getProduct(@PathVariable("id") UUID id);

    // 상품 재고 차감
    @PutMapping("/api/v1/products/{id}")
    void decreaseStock(@PathVariable("id") UUID id, @RequestBody int productQuantity);

}
