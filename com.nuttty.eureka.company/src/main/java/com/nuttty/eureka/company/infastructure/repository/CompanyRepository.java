package com.nuttty.eureka.company.infastructure.repository;

import com.nuttty.eureka.company.domain.model.Company;
import com.nuttty.eureka.company.domain.repository.CompanyRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID>, CompanyRepositoryCustom {

    @Query("select count(c) > 0 from Company c where c.address = :address")
    boolean existsByAddress(@Param("address") String address);

    @Query("select count(c) > 0 from Company c where c.userId = :userId")
    boolean existsByUserId(@Param("userId") Long userId);

    @Query("select count(c) > 0 from Company c where c.userId = :userId and c.id <> :companyId")
    boolean existsByUserIdAndExcludeId(@Param("userId") Long userId,@Param("companyId") UUID companyId);

    @Query("select count(c) > 0 from Company c where c.address = :address and c.id <> :companyId")
    boolean existsByAddressAndExcludeId(@Param("address") String address, @Param("companyId") UUID companyId);

    @Query("select c from Company c where c.id = :id and c.hubId = :hubId")
    Optional<Company> findByCompanyIdAndHubId(@Param("id") UUID companyId, @Param("hubId") UUID hubId);

    @Query("select c from Company c where c.userId = :userId")
    Optional<Company> findByUserId(@Param("userId") Long userId);

    @Query("select c from Company c where c.id = :id and c.hubId = :hubId and c.userId = :userId")
    Optional<Company> findByCompanyIdAndHubIdAndUserId(@Param("id") UUID companyId,@Param("hubId") UUID hubId,@Param("userId") Long userId);

    List<Company> findByIdIn(List<UUID> id);
}
