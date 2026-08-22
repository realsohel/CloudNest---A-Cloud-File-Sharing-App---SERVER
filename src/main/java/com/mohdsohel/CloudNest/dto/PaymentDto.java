package com.mohdsohel.CloudNest.dto;

import com.mohdsohel.CloudNest.document.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {

    private String planId; // to mention
    private String orderId;
    private Integer amount; // to mention
    private String currency; // to mention
    private Integer credits;
    private Boolean success;
    private String message;
    private PaymentStatus paymentStatus;
    private String userEmail;
    private String userName;
    private LocalDateTime paymentDate;
}
