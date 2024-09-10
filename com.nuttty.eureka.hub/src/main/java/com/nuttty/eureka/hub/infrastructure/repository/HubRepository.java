package com.nuttty.eureka.hub.infrastructure.repository;

import com.nuttty.eureka.hub.domain.model.Hub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID> {

    @Query("select count(h) > 0 from Hub h where (h.address = :address or h.name = :name) and h.isDelete = false")
    boolean existsByAddressOrName(@Param("address") String address, @Param("name") String name);

    @Query("select count(h) > 0 from Hub h where (h.name = :name or h.address = :address) and h.id <> :id and h.isDelete = false")
    boolean existsByNameOrAddressExcludingId(@Param("name") String name,@Param("address") String address,@Param("id") UUID hubId);

    @Override
    @Query("select h from Hub h where h.id = :id and h.isDelete = false")
    Optional<Hub> findById(@Param("id") UUID hubId);

    @Query("select count(h) > 0 from Hub h where h.userId = :userId and h.id <> :id and h.isDelete = false")
    boolean existsByIdAndUserId(@Param("userId") Long userId,@Param("id") UUID hubId);

    @Query("select count(h) > 0 from Hub h where h.userId = :userId and h.isDelete = false")
    boolean existsByUserId(@Param("userId") Long userId);

}
