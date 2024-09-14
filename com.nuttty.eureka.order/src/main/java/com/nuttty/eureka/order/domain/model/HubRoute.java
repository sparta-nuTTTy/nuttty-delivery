package com.nuttty.eureka.order.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_hub_routes")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubRoute extends AuditEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "hub_route_id", updatable = false, nullable = false)
    private UUID hubRouteId;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "arrival_hub_id", nullable = false)
    private UUID arrivalHubId;

    @Column(name = "duration", nullable = false)
    private LocalDateTime duration;

    @Column(name = "route_info", nullable = false)
    private String routeInfo;

    @OneToMany(mappedBy = "hubRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryRoute> deliveryRoutes = new ArrayList<>();

    public static HubRoute create(UUID departureHubId, UUID arrivalHubId, LocalDateTime duration, String routeInfo) {
        return HubRoute.builder()
                .departureHubId(departureHubId)
                .arrivalHubId(arrivalHubId)
                .duration(duration)
                .routeInfo(routeInfo)
                .build();
    }
}
