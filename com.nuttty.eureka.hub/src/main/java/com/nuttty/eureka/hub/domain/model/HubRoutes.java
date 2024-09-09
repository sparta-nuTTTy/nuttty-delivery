package com.nuttty.eureka.hub.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_hub_routes")
public class HubRoutes extends AuditEntity{

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "hub_route_id")
    private UUID id;

    @Column(name = "departure_hub_id")
    private UUID departureHubId;

    @Column(name = "arrival_hub_id")
    private UUID arrivalHubId;

    private LocalTime duration;

    @Column(name = "route_info")
    private String routeInfo;
}
