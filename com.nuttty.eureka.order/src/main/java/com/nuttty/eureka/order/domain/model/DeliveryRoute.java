package com.nuttty.eureka.order.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_routes")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryRoute extends AuditEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "delivery_route_id", updatable = false, nullable = false)
    private UUID deliveryRouteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    // 경로 순서
    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    // 배송 담당자 ID
    @Column(name = "delivery_person_id")
    private Long deliveryPersonId;

    // 허브간 이동 정보 테이블(허브간 이동 고정 경로)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_route_id", nullable = false)
    private HubRoute hubRoute;

    public static DeliveryRoute create(Delivery delivery, HubRoute hubRoute, int orderIndex) {
        return DeliveryRoute.builder()
                .delivery(delivery)
                .hubRoute(hubRoute)
                .orderIndex(orderIndex)
                .build();
    }

    public void updateDeliveryPerson(Long deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
    }
}
