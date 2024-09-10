package com.nuttty.eureka.company.domain.model;

import com.nuttty.eureka.company.presentation.request.CompanyRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_companies")
@SQLRestriction("is_delete = false")
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

    public Company(String name, CompanyType type, String address, Long userId, UUID hubId) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.userId = userId;
        this.hubId = hubId;
    }

    /**
     * 수정 메서드
     * @param request
     * @return
     */
    public Company update(CompanyRequestDto request) {
        userId = request.getUser_id();
        hubId = request.getHub_id();
        name = request.getName();
        type = CompanyType.valueOf(request.getType());
        address = request.getAddress();
        return this;
    }
}
