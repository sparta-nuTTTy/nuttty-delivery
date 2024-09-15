package com.nuttty.eureka.ai.application.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

    @JsonProperty("company_id")
    private UUID companyId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("hub_id")
    private UUID hubId;

    private String name;
    private String type;
    private String address;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("updated_by")
    private String updatedBy;
}
