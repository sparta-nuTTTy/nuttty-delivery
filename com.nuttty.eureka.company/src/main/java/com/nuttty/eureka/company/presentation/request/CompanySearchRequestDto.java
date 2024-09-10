package com.nuttty.eureka.company.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanySearchRequestDto {

    private UUID company_id;
    private Long user_id;
    private UUID hub_id;

    private String name;
    private String type;
    private String address;
}
