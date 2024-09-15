package com.nuttty.eureka.ai.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSupplierDto {
    private UUID supplierId;
    private String supplierName;
}
