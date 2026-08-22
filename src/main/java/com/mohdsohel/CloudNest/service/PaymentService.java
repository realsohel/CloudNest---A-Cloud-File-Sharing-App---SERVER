package com.mohdsohel.CloudNest.service;

import com.mohdsohel.CloudNest.dto.PaymentDto;
import com.mohdsohel.CloudNest.dto.PaymentVerificationDto;

public interface PaymentService {

    PaymentDto createOrder(PaymentDto paymentDto);
    PaymentDto verifyPayment(PaymentVerificationDto paymentVerificationDto);

}
