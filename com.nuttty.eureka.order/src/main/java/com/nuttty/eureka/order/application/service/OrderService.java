package com.nuttty.eureka.order.application.service;

import com.nuttty.eureka.order.domain.model.Order;
import com.nuttty.eureka.order.domain.service.OrderDomainService;
import com.nuttty.eureka.order.application.fegin.CompanyClient;
import com.nuttty.eureka.order.application.fegin.ProductClient;
import com.nuttty.eureka.order.application.fegin.dto.CompanyInfoDto;
import com.nuttty.eureka.order.application.fegin.dto.ProductInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static com.nuttty.eureka.order.presentation.dto.OrderProductDto.*;
import static com.nuttty.eureka.order.presentation.dto.OrederDto.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final ProductClient productClient;
    private final CompanyClient companyClient;
    private final OrderDomainService orderDomainService;

    // 주문생성
    @Transactional
    public OrderResponseDto createOrder(OrderCreateDto orderCreateDto) {
        // 외부 서비스 호출: Company 서비스에서 회사 정보 조회
        log.info("회사 정보 조회 시작 : supplierId = {}, receiverId = {}", orderCreateDto.getSupplierId(), orderCreateDto.getReceiverId());
        CompanyInfoDto supplierCompany = companyClient.getCompany(orderCreateDto.getSupplierId());
        CompanyInfoDto receiverCompany = companyClient.getCompany(orderCreateDto.getReceiverId());

        // 외부 서비스 호출: Product 서비스에서 상품 정보 조회(재고 확인) 및 재고 차감, 재고가 없을 경우 트랜잭션 롤백 처리 필요(학습후 구현 예정)
        log.info("상품 정보 조회 및 재고 확인 시작 : productItems = {}", orderCreateDto.getProductItems());
        orderCreateDto.getProductItems().forEach(orderItem -> {
            ProductInfoDto product = productClient.getProduct(orderItem.getProductId());

            // 재고 확인
            if (product.getData().getProductQuantity() < orderItem.getOrderAmount()) {
                throw new RuntimeException("재고 부족");
            }

            // 재고 차감
            productClient.decreaseStock(orderItem.getProductId(), orderItem.getOrderAmount());
        });

        // 허브 경로 정보 조회 및 생성(학습후 구현 예정)


        // 주문 생성
        Order order = orderDomainService.createOrder(orderCreateDto,
                supplierCompany.getData().getHubId(),
                receiverCompany.getData().getHubId(),
                receiverCompany.getData().getAddress(),
                receiverCompany.getData().getName());


        // 응답 DTO 생성
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .supplierId(order.getSupplierId())
                .receiverId(order.getReceiverId())
                .productItems(order.getOrderProducts().stream()
                        .map(orderProduct -> OrderItem.builder()
                                .productId(orderProduct.getProductId())
                                .productPrice(orderProduct.getProductPrice())
                                .orderAmount(orderProduct.getOrderAmount())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
