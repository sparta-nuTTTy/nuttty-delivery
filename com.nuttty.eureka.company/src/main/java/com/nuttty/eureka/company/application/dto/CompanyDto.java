package com.nuttty.eureka.company.application.dto;

import com.nuttty.eureka.company.domain.model.Company;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CompanyDto {

    private UUID company_id;
    private Long user_id;
    private UUID hub_id;

    private String name;
    private String type;
    private String address;

    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;

    @QueryProjection
    public CompanyDto(UUID company_id, Long user_id, UUID hub_id, String name, String type, String address, LocalDateTime created_at, String created_by, LocalDateTime updated_at, String updated_by) {
        this.company_id = company_id;
        this.user_id = user_id;
        this.hub_id = hub_id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.created_at = created_at;
        this.created_by = created_by;
        this.updated_at = updated_at;
        this.updated_by = updated_by;
    }

    public static CompanyDto toDto(Company savedCompany) {
        return new CompanyDto(
                savedCompany.getId(),
                savedCompany.getUserId(),
                savedCompany.getHubId(),
                savedCompany.getName(),
                savedCompany.getType().name(),
                savedCompany.getAddress(),
                savedCompany.getCreatedAt(),
                savedCompany.getCreatedBy(),
                savedCompany.getUpdatedAt(),
                savedCompany.getUpdatedBy()
        );
    }
}
