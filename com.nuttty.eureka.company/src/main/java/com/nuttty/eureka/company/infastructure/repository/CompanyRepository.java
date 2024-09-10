package com.nuttty.eureka.company.infastructure.repository;

import com.nuttty.eureka.company.domain.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    @Query("select count(c) > 0 from Company c where c.address = :address")
    boolean existsByAddress(@Param("address") String address);

    @Query("select count(c) > 0 from Company c where c.userId = :userId")
    boolean existsByUserId(@Param("userId") Long userId);

    @Query("select count(c) > 0 from Company c where c.userId = :userId and c.id <> :companyId")
    boolean existsByUserIdAndExcludeId(@Param("userId") Long userId,@Param("companyId") UUID companyId);

    @Query("select count(c) > 0 from Company c where c.address = :address and c.id <> :companyId")
    boolean existsByAddressAndExcludeId(@Param("address") String address, @Param("companyId") UUID companyId);
}
