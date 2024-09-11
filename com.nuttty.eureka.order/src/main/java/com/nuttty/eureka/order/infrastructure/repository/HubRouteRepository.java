package com.nuttty.eureka.order.infrastructure.repository;

import com.nuttty.eureka.order.domain.model.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {
}
