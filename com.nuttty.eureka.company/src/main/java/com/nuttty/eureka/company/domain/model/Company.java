package com.nuttty.eureka.company.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_companies")
public class Company extends AuditEntity{

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "company_id")
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CompanyType type;

    private String address;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "hub_id")
    private UUID hubId;

    @OneToMany(mappedBy = "company")
    private List<Product> products = new ArrayList<>();


}
