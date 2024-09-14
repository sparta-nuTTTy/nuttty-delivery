package com.nuttty.eureka.order.domain.service;

import com.nuttty.eureka.order.domain.model.*;
import com.nuttty.eureka.order.presentation.dto.OrederDto.OrderCreateDto;
import com.nuttty.eureka.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j(topic = "OrderDomainService")
@RequiredArgsConstructor
public class OrderDomainService {
    private final OrderRepository orderRepository;

    // 주문생성, 주문상품 생성, 배송 및 배송경로 생성
    @Transactional
    public Order createOrder(OrderCreateDto orderCreateDto,
                             UUID supplierHubId,
                             UUID receiverHubId,
                             String receiverCompanyAddress,
                             String receiverComapnyName,
                             List<HubRoute> hubRoutes) {

        // 주문 생성(Order)
        log.info("주문 생성 시작 : receiverId = {}, supplierId = {}", orderCreateDto.getReceiverId(), orderCreateDto.getSupplierId());
        Order order = Order.createOrder(
                orderCreateDto.getReceiverId(),
                orderCreateDto.getSupplierId()
        );

        // 주문 상품 생성(OrderProduct)
        log.info("주문 상품 생성 시작 : productItems = {}", orderCreateDto.getProductItems());
        List<OrderProduct> orderProducts = orderCreateDto.getProductItems().stream()
                .map(productItem -> OrderProduct.createOrderProduct(
                        order,
                        productItem.getProductId(),
                        productItem.getProductPrice(),
                        productItem.getOrderAmount()))
                .toList();

        // 주문 상품을 주문에 추가
        order.addOrderProducts(orderProducts);

        // 배송(Delivery) 생성
        Delivery delivery = Delivery.createDelivery(
                supplierHubId,
                receiverHubId,
                receiverCompanyAddress,
                receiverComapnyName
        );

        // 허브 경로 리스트를 이용해 DeliveryRoute 생성
        delivery.addDeliveryRoutes(hubRoutes);

        // 주문에 배송 설정
        order.setDelivery(delivery);

        return orderRepository.save(order);
    }

    // 주문 단건 조회
    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId = " + orderId));
    }
}
