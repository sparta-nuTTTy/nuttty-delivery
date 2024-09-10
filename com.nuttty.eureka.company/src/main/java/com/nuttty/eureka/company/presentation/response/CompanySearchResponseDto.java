package com.nuttty.eureka.company.presentation.response;

import com.nuttty.eureka.company.application.dto.CompanyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanySearchResponseDto {

    private int status_code;
    private String result_message;
    private List<CompanyDto> companyDtoList;
}
