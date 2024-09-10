package com.nuttty.eureka.company.application.dto;

import com.nuttty.eureka.company.domain.model.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
