package com.mohdsohel.CloudNest.dto;

import com.mohdsohel.CloudNest.document.enums.Plans;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreditsDto {

    private Integer credits;
    private Plans plans;

}