package com.mohdsohel.CloudNest.repository;

import com.mohdsohel.CloudNest.document.PaymentTransaction;
import com.mohdsohel.CloudNest.document.enums.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentTransaction, String> {

    List<PaymentTransaction> findByClerkId(String clerkId);
    List<PaymentTransaction> findByClerkIdOrderByPaymentTransactionDateDesc(String clerkId);
    List<PaymentTransaction> findByClerkIdAndStatusOrderByPaymentTransactionDateDesc(String clerkId, PaymentStatus status);
}
