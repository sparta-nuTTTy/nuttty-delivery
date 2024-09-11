package com.nuttty.eureka.company.infastructure.repository;

import com.nuttty.eureka.company.domain.model.Product;
import com.nuttty.eureka.company.domain.repository.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, ProductRepositoryCustom {

    @Query("select count(p) > 0 from Product p where p.productName = :name and p.company.id = :companyId and p.hubId = :hubId")
    boolean existsByNameAndCompanyIdAndHubId(@Param("name") String productName, @Param("companyId") UUID companyId, @Param("hubId") UUID hubId);

    @Query("select count(p) > 0 from Product p where p.productName = :name and p.id <> :id")
    boolean existsByProductNameExcludeId(@Param("name") String productName,@Param("id") UUID productId);
}
