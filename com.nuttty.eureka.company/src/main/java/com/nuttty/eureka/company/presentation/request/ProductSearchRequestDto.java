package com.nuttty.eureka.company.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchRequestDto {

    private UUID product_id;
    private UUID company_id;
    private UUID hub_id;

    private String product_name;

}
