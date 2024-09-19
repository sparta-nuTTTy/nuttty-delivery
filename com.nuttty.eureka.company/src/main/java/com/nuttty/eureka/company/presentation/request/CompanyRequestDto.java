package com.nuttty.eureka.company.presentation.request;

import com.nuttty.eureka.company.domain.model.Company;
import com.nuttty.eureka.company.domain.model.CompanyType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "허브 업체 유저 ID", example = "1", type = "integer", format = "int64")
    private Long user_id;

    @NotNull(message = "Hub ID must be provided")
    @Schema(description = "허브 ID", example = "23a39928-7bed-46c0-84d5-ce0b9c3b2eec", type = "string", format = "uuid")
    private UUID hub_id;

    @NotBlank(message = "Company Name must be provided")
    @NotNull(message = "Company Name must be provided")
    @Schema(description = "업체 이름", example = "업체 이름 A", type = "string")
    private String name;

    @NotBlank(message = "Company Type must be provided")
    @NotNull(message = "Company Type must be provided")
    @Pattern(regexp = "^(MANUFACTURER|RECEIPT_COMPANY)$", message = "Company Type must be either 'MANUFACTURER' or 'RECEIPT_COMPANY'")
    @Schema(description = "업체 종류", example = "MANUFACTURER", type = "string")
    private String type;

    @NotNull(message = "Company Address must be provided")
    @NotBlank(message = "Company Address must be provided")
    @Schema(description = "업체 주소", example = "서울특별시 종로구 555", type = "string")
    private String address;

    public Company toEntity() {
        return new Company(name, CompanyType.valueOf(type), address, user_id, hub_id);
    }
}
