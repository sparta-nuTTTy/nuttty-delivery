package com.nuttty.eureka.hub.infrastructure.repository;

import com.nuttty.eureka.hub.domain.model.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID> {

    Optional<Hub> findByAddress(String address);

    Optional<Hub> findByName(String name);
}
