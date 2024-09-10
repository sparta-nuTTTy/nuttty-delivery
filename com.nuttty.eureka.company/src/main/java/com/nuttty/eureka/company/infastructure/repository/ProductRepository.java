package com.nuttty.eureka.company.infastructure.repository;

import com.nuttty.eureka.company.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("select count(p) > 0 from Product p where p.productName = :name")
    boolean existsByProductName(@Param("name") String productName);

    @Query("select count(p) > 0 from Product p where p.productName = :name and p.id <> :id")
    boolean existsByProductNameExcludeId(@Param("name") String productName,@Param("id") UUID productId);
}
