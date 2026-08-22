package com.mohdsohel.CloudNest.document;

import com.mohdsohel.CloudNest.document.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "payment_transactions")
public class PaymentTransaction {

    @Id
    private String id;

    private String clerkId;
    private String orderId;
    private String paymentId;
    private String planId;
    private Integer amount;
    private String currency;
    private Integer credits;
    private PaymentStatus status;
    private String userEmail;
    private String userName;
    private LocalDateTime paymentTransactionDate;
}
