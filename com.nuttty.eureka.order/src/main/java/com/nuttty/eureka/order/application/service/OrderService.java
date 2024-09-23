package com.nuttty.eureka.order.application.service;

import com.nuttty.eureka.order.domain.model.DeliveryRoute;
import com.nuttty.eureka.order.domain.model.HubRoute;
import com.nuttty.eureka.order.domain.model.Order;
import com.nuttty.eureka.order.domain.service.OrderDomainService;
import com.nuttty.eureka.order.application.feign.CompanyClient;
import com.nuttty.eureka.order.application.feign.dto.CompanyInfoDto;
import com.nuttty.eureka.order.application.feign.dto.ProductInfoDto;
import com.nuttty.eureka.order.exception.exceptionsdefined.ExternalServiceException;
import com.nuttty.eureka.order.exception.exceptionsdefined.InsufficientStockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nuttty.eureka.order.presentation.dto.OrderProductDto.*;
import static com.nuttty.eureka.order.presentation.dto.OrederDto.*;

@Service
@Slf4j(topic = "OrderService")
@RequiredArgsConstructor
public class OrderService {
    private final CompanyClient companyClient;
    private final OrderDomainService orderDomainService;
    private final HubRouteService hubRouteService;

    // 주문생성
    @Transactional
    public OrderCreateResponseDto createOrder(OrderCreateDto orderCreateDto) {
        // 외부 서비스 호출: Company 서비스에서 회사 정보 조회
        log.info("회사 정보 조회 시작 : supplierId = {}, receiverId = {}", orderCreateDto.getSupplierId(), orderCreateDto.getReceiverId());
        CompanyInfoDto supplierCompany = companyClient.getCompany(orderCreateDto.getSupplierId(), "19092");
        CompanyInfoDto receiverCompany = companyClient.getCompany(orderCreateDto.getReceiverId(), "19092");

        if (supplierCompany == null || receiverCompany == null) {
            throw new ExternalServiceException(HttpStatus.NOT_FOUND, "회사 정보를 찾을 수 없습니다.");
        }

        // 외부 서비스 호출: Product 서비스에서 상품 정보 조회(재고 확인) 및 재고 차감, 재고가 없을 경우 트랜잭션 롤백 처리 필요(학습후 구현 예정)
        log.info("상품 정보 조회 및 재고 확인 시작 : productItems = {}", orderCreateDto.getProductItems());
        orderCreateDto.getProductItems().forEach(orderItem -> {
            ProductInfoDto product = companyClient.getProduct(orderItem.getProductId(), "19092");
            // 상품 가격 확인
            if (!product.getProductDto().getProductPrice().equals(orderItem.getProductPrice())) {
                throw new ExternalServiceException(HttpStatus.BAD_REQUEST, "상품 가격이 일치하지 않습니다. 상품 ID: " + product.getProductDto().getProductId());
            }

            // 재고 확인
            if (product.getProductDto().getProductQuantity() < orderItem.getOrderAmount()) {
                throw new InsufficientStockException("재고가 부족합니다. 상품 ID: " + product.getProductDto().getProductId());
            }

            // 재고 차감
            Integer decreaseStock = companyClient.decreaseStock(orderItem.getProductId(), orderItem.getOrderAmount(), "19092");
            log.info("상품 재고 차감 완료 : productId = {}, orderAmount = {}", orderItem.getProductId(), orderItem.getOrderAmount());
            log.info("상품 재고 차감 내용 : decreaseStock = {}", decreaseStock);
        });

        // 허브 경로 조회(공급 업체 허브부터 수신 업체 허브까지 경로 조회)
        log.info("허브 경로 조회 시작: supplierHubId = {}, receiverHubId = {}", supplierCompany.getData().getHubId(), receiverCompany.getData().getHubId());
        List<HubRoute> allHubRoutes = hubRouteService.findAllHubRoutes(supplierCompany.getData().getHubId(), receiverCompany.getData().getHubId());
        log.info("허브 경로 조회 완료: allHubRoute = {}", allHubRoutes.toString());

        // 주문 생성
        Order order = orderDomainService.createOrder(orderCreateDto,
                supplierCompany.getData().getHubId(),
                receiverCompany.getData().getHubId(),
                receiverCompany.getData().getAddress(),
                receiverCompany.getData().getName(),
                allHubRoutes);


        // 응답 DTO 생성
        return OrderCreateResponseDto.builder()
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

    // 주문 단건 조회
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(UUID orderId) {
        // 주문 조회
        Order order = orderDomainService.getOrder(orderId);

        // 외부 서비스 호출: Company 서비스에서 회사 정보 조회
        log.info("회사 정보 조회 시작 : supplierId = {}, receiverId = {}", order.getSupplierId(), order.getReceiverId());
        CompanyInfoDto supplierCompany = companyClient.getCompany(order.getSupplierId(), "19092");
        CompanyInfoDto receiverCompany = companyClient.getCompany(order.getReceiverId(), "19092");

        if (supplierCompany == null || receiverCompany == null) {
            throw new ExternalServiceException(HttpStatus.NOT_FOUND, "회사 정보를 찾을 수 없습니다.");
        }

        // 주문 상품 -> Dto 변환
        List<OrderItem> orderItems = order.getOrderProducts().stream()
                .map(orderProduct -> OrderItem.builder()
                        .productId(orderProduct.getProductId())
                        .productPrice(orderProduct.getProductPrice())
                        .orderAmount(orderProduct.getOrderAmount())
                        .build())
                .toList();

        // 배송 경로(DeliveryRoute) -> Dto 변환
        // orderIndex 순서대로 배송 경로를 조회하여 리스트로 변환
        List<UUID> deliveryRouteIds = order.getDelivery().getDeliveryRoutes().stream()
                .sorted(Comparator.comparingInt(DeliveryRoute::getOrderIndex)) // orderIndex 순서대로 정렬
                .map(DeliveryRoute::getDeliveryRouteId)
                .toList();

        // 응답 DTO 생성
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .orderTotalPrice(order.getTotalPrice())
                .supplierId(order.getSupplierId())
                .supplierName(supplierCompany.getData().getName())
                .receiverId(order.getReceiverId())
                .deliveryId(order.getDelivery().getDeliveryId())
                .receiverName(receiverCompany.getData().getName())
                .deliveryAddress(order.getDelivery().getDeliveryAddress())
                .deliveryStatus(order.getDelivery().getDeliveryStatus())
                .productItems(orderItems)
                .deliveryRoutes(deliveryRouteIds)
                .build();
    }

    // 주문 동적 검색 및 페이징 조회
    public Page<OrderResponseDto> searchOrders(OrderSearchDto condition, Pageable pageable) {
        return orderDomainService.searchOrders(condition, pageable);
    }

    @Transactional
    public OrderCancelResponseDto cancelOrder(UUID orderId, String email) {
        return orderDomainService.cancelOrder(orderId, email);
    }

    @Transactional
    public OrderUpdateResponseDto updateOrder(UUID orderId, OrderUpdateDto orderUpdateDto) {
        return orderDomainService.updateOrder(orderId, orderUpdateDto);
    }
}
