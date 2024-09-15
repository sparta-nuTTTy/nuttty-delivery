package com.nuttty.eureka.order.infrastructure.repository;

import com.nuttty.eureka.order.application.fegin.CompanyClient;
import com.nuttty.eureka.order.application.fegin.dto.CompanyInfoDto;
import com.nuttty.eureka.order.domain.model.*;
import com.nuttty.eureka.order.domain.repository.OrderRepositoryCustom;
import com.nuttty.eureka.order.presentation.dto.DeliveryDto;
import com.nuttty.eureka.order.presentation.dto.OrederDto.OrderSearchDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.nuttty.eureka.order.domain.model.QDelivery.*;
import static com.nuttty.eureka.order.domain.model.QDeliveryRoute.*;
import static com.nuttty.eureka.order.domain.model.QOrder.*;
import static com.nuttty.eureka.order.presentation.dto.OrderProductDto.*;
import static com.nuttty.eureka.order.presentation.dto.OrederDto.*;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final CompanyClient companyClient;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderResponseDto> findSearchOrders(OrderSearchDto condition, Pageable pageable) {
        List<Order> orders = queryFactory
                .selectFrom(order)
                .where(orderIdEq(condition.getOrderId()),
                        receiverIdEq(condition.getReceiverId()),
                        orderStatusEq(condition.getStatus()),
                        createdAtGoeAndLoe(condition.getStartDate(), condition.getEndDate()),
                        order.isDelete.eq(false))
                .offset(pageable.getOffset())
                .orderBy(getSortOrder(pageable.getSort()))
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리 지연 로딩
        JPAQuery<Order> count = queryFactory
                .selectFrom(order)
                .where(orderIdEq(condition.getOrderId()),
                        receiverIdEq(condition.getReceiverId()),
                        orderStatusEq(condition.getStatus()),
                        createdAtGoeAndLoe(condition.getStartDate(), condition.getEndDate()),
                        order.isDelete.eq(false));


        // 외부 서비스 호출 및 데이터 결합
        List<OrderResponseDto> result = orders.stream()
                .map(this::mapToOrderResponseDto)
                .toList();

        return PageableExecutionUtils.getPage(result, pageable, count::fetchCount);
}

    @Override
    public Page<DeliveryDto.DeliveryResponseDto> findOrdersByCondition(DeliveryDto.DeliverySaerch condition, Pageable pageable) {
        // 배송 정보 조회 쿼리
        List<Order> orders = queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery)
                .leftJoin(delivery.deliveryRoutes, deliveryRoute)
                .where(
                        deliveryIdEq(condition.getDeliveryId()),
                        orderIdEq(condition.getOrderId()),
                        departureHubIdEq(condition.getDepartureHubId()),
                        deliveryAddressEq(condition.getDeliveryAddress()),
                        deliveryStatusEq(condition.getDeliveryStatus()),
                        createdAtDeliveryGoeAndLoe(condition.getStartDate(), condition.getEndDate()),
                        order.isDelete.eq(false))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSortOrder(pageable.getSort()))
                .fetch();

        // 카운트 쿼리 지연 로딩
        JPAQuery<Order> count = queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery)
                .where(
                        deliveryIdEq(condition.getDeliveryId()),
                        orderIdEq(condition.getOrderId()),
                        departureHubIdEq(condition.getDepartureHubId()),
                        deliveryAddressEq(condition.getDeliveryAddress()),
                        deliveryStatusEq(condition.getDeliveryStatus()),
                        createdAtDeliveryGoeAndLoe(condition.getStartDate(), condition.getEndDate()),
                        order.isDelete.eq(false));

        // 결과 Dto로 변환
        List<DeliveryDto.DeliveryResponseDto> list = orders.stream()
                .map(order -> DeliveryDto.DeliveryResponseDto.builder()
                        .deliveryId(order.getDelivery().getDeliveryId())
                        .orderId(order.getOrderId())
                        .deliveryPersonId(order.getDelivery().getDeliveryPersonId())
                        .departureHubId(order.getDelivery().getDepartureHubId())
                        .arrivalHubId(order.getDelivery().getArrivalHubId())
                        .deliveryAddress(order.getDelivery().getDeliveryAddress())
                        .deliveryReceiver(order.getDelivery().getDeliveryReceiver())
                        .deliveryStatus(order.getDelivery().getDeliveryStatus())
                        .deliveryRouteIds(order.getDelivery().getDeliveryRoutes().stream()
                                .map(DeliveryRoute::getDeliveryRouteId)
                                .toList())
                        .build())
                .toList();

        return PageableExecutionUtils.getPage(list, pageable, count::fetchCount);
    }


    // 정렬 조건
    private OrderSpecifier<?>[] getSortOrder(Sort sort) {
        return sort.stream()
                .map(order -> {
                    switch (order.getProperty()) {
                        case "createdAt" -> {
                            return order.getDirection().isAscending() ? QOrder.order.createdAt.asc() : QOrder.order.createdAt.desc();
                        }
                        case "updatedAt" -> {
                            return order.getDirection().isAscending() ? QOrder.order.updatedAt.asc() : QOrder.order.updatedAt.desc();
                        }
                        default -> {
                            return null; // 유효 하지 않은 정렬 조건은 무시
                        }
                    }
                })
                .filter(Objects::nonNull)
                .toArray(OrderSpecifier[]::new);
    }

    private OrderResponseDto mapToOrderResponseDto(Order order) {
        // 회사 정보 조회
        CompanyInfoDto supplierCompany = companyClient.getCompany(order.getSupplierId());
        CompanyInfoDto receiverCompany = companyClient.getCompany(order.getReceiverId());

        // OrderProduct를 OrderItem으로 변환
        List<OrderItem> orderItems = order.getOrderProducts().stream()
                .map(orderProduct -> OrderItem.builder()
                        .productId(orderProduct.getProductId())
                        .productPrice(orderProduct.getProductPrice())
                        .orderAmount(orderProduct.getOrderAmount())
                        .build())
                .toList();

        // 배송 경로를 UUID 리스트로 변환
        List<UUID> uuidList = order.getDelivery().getDeliveryRoutes().stream()
                .map((DeliveryRoute::getDeliveryRouteId))
                .toList();

        // 최종 OrderResponseDto 생성
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .orderTotalPrice(order.getTotalPrice())
                .supplierId(order.getSupplierId())
                .supplierName(supplierCompany != null ? supplierCompany.getData().getName() : "Unknown")
                .receiverId(order.getReceiverId())
                .deliveryId(order.getDelivery().getDeliveryId())
                .receiverName(receiverCompany != null ? receiverCompany.getData().getName() : "Unknown")
                .deliveryAddress(order.getDelivery().getDeliveryAddress())
                .deliveryStatus(order.getDelivery().getDeliveryStatus())
                .productItems(orderItems)
                .deliveryRoutes(uuidList)
                .build();
    }

    private BooleanExpression createdAtGoeAndLoe(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null ? order.createdAt.between(startDate, endDate) : null;
    }

    private BooleanExpression orderStatusEq(String status) {
        return StringUtils.hasText(status) ? order.orderStatus.eq(OrderStatus.valueOf(status)) : null;
    }

    private BooleanExpression receiverIdEq(UUID receiverId) {
        return receiverId != null ? order.receiverId.eq(receiverId) : null;
    }

    private BooleanExpression orderIdEq(UUID orderId) {
        return orderId != null ? order.orderId.eq(orderId) : null;
    }

    private BooleanExpression deliveryIdEq(UUID deliveryId) {
        return deliveryId != null ? order.delivery.deliveryId.eq(deliveryId) : null;
    }

    private BooleanExpression departureHubIdEq(UUID departureHubId) {
        return departureHubId != null ? order.delivery.departureHubId.eq(departureHubId) : null;
    }

    private BooleanExpression deliveryAddressEq(String deliveryAddress) {
        return StringUtils.hasText(deliveryAddress) ? order.delivery.deliveryAddress.eq(deliveryAddress) : null;
    }

    private BooleanExpression deliveryStatusEq(DeliveryStatus deliveryStatus) {
        return deliveryStatus != null ? order.delivery.deliveryStatus.eq(deliveryStatus) : null;
    }

    private BooleanExpression createdAtDeliveryGoeAndLoe(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null ? delivery.createdAt.between(startDate, endDate) : null;
    }

}
