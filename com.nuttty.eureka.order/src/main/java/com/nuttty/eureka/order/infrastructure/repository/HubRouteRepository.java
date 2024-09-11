package com.nuttty.eureka.order.infrastructure.repository;

import com.nuttty.eureka.order.domain.model.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {
    @Query("SELECT hr FROM HubRoute hr WHERE hr.departureHubId = :departureHubId")
    HubRoute findByDepartureHubId(@Param("departureHubId") UUID departureHubId);
}
