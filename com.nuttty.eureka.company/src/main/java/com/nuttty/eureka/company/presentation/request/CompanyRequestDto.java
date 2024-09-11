package com.nuttty.eureka.company.presentation.request;

import com.nuttty.eureka.company.domain.model.Company;
import com.nuttty.eureka.company.domain.model.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequestDto {

    @NotNull(message = "User ID must be provided")
    private Long user_id;

    @NotNull(message = "Hub ID must be provided")
    private UUID hub_id;

    @NotBlank(message = "Company Name must be provided")
    @NotNull(message = "Company Name must be provided")
    private String name;

    @NotBlank(message = "Company Type must be provided")
    @NotNull(message = "Company Type must be provided")
    @Pattern(regexp = "^(MANUFACTURER|RECEIPT_COMPANY)$", message = "Company Type must be either 'MANUFACTURER' or 'RECEIPT_COMPANY'")
    private String type;

    @NotNull(message = "Company Address must be provided")
    @NotBlank(message = "Company Address must be provided")
    private String address;

    public Company toEntity() {
        return new Company(name, CompanyType.valueOf(type), address, user_id, hub_id);
    }
}
