package com.nuttty.eureka.auth.infrastructure.repository;

import com.nuttty.eureka.auth.domain.model.DeliveryPerson;
import com.nuttty.eureka.auth.domain.repository.DeliveryPersonRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long>, DeliveryPersonRepositoryCustom {
}
