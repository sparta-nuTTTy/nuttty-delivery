package com.nuttty.eureka.order.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

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
    private String deliveryRouteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    // 자기 참조 관계로 다음 경로 정보를 가짐
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_delivery_route_id")
    private DeliveryRoute nextDeliveryRoute;

    // 허브간 이동 정보 테이블(허브간 이동 고정 경로)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_route_id", nullable = false)
    private HubRoute hubRoute;


    public static DeliveryRoute create(Delivery delivery, DeliveryRoute nextDeliveryRoute, HubRoute hubRoute) {
        return DeliveryRoute.builder()
                .delivery(delivery)
                .nextDeliveryRoute(nextDeliveryRoute)
                .hubRoute(hubRoute)
                .build();
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }
}
