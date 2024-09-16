package com.nuttty.eureka.order.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_deliveries")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends AuditEntity {
    @Id
    @UuidGenerator
    @Column(name = "delivery_id", updatable = false, nullable = false)
    private UUID deliveryId;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "arrival_hub_id", nullable = false)
    private UUID arrivalHubId;

    @Column(name = "delivery_person_id")
    private Long deliveryPersonId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_recevier", nullable = false)
    private String deliveryReceiver;

    // Delivery와 DlieveryRuote 연관관계
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeliveryRoute> deliveryRoutes = new ArrayList<>();

    public static Delivery createDelivery(UUID departureHubId, UUID arrivalHubId, String deliveryAddress, String deliveryReceiver) {
        return Delivery.builder()
                .departureHubId(departureHubId)
                .arrivalHubId(arrivalHubId)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryAddress(deliveryAddress)
                .deliveryReceiver(deliveryReceiver)
                .build();
    }

    // 배송 상태 변경
    public void changeDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    // 배송 경로 리스트 추가
    public void addDeliveryRoutes(List<HubRoute> hubRoutes) {
        int orderIdx = 0;

        for (HubRoute hubRoute : hubRoutes) {
            deliveryRoutes.add(DeliveryRoute.create(this, hubRoute, orderIdx++));
        }
    }

    // 배송 담당자 배정 및 변경
    public void changeDeliveryPersonId(Long deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
    }
}
