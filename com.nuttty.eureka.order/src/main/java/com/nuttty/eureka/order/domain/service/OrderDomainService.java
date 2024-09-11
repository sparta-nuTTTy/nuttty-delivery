package com.nuttty.eureka.order.domain.service;

import com.nuttty.eureka.order.application.dto.OrederDto.OrderCreateDto;
import com.nuttty.eureka.order.domain.model.Delivery;
import com.nuttty.eureka.order.domain.model.Order;
import com.nuttty.eureka.order.domain.model.OrderProduct;
import com.nuttty.eureka.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderDomainService {
    private final OrderRepository orderRepository;

    // 주문생성, 주문상품 생성, 배송 및 배송경로 생성
    @Transactional
    public Order createOrder(OrderCreateDto orderCreateDto,
                             UUID supplierHubId,
                             UUID receiverHubId,
                             String receiverCompanyAddress,
                             String receiverComapnyName) {

        // 주문 생성(Order)
        Order order = Order.createOrder(
                orderCreateDto.getReceiverId(),
                orderCreateDto.getSupplierId()
        );

        // 주문 상품 생성(OrderProduct)
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

        // 주문에 배송 설정
        order.setDelivery(delivery);

        /*
         배송 경로 기록(DeliveryRoute) 생성
         배송 경로 기록은 배송(Delivery) 생성시 생성되도록 구현
         배송 경로 기록은 허브간 이동 정보 테이블(p_hub_route)을 보고 생성되도록 구현
                DeliveryRoute deliveryRoute = DeliveryRoute.create(
                        delivery,
                        null,
                        );
                // 배송에 배송 경로 설정
                delivery.addDeliveryRoute(deliveryRoute);
        */

        return orderRepository.save(order);
    }
}
