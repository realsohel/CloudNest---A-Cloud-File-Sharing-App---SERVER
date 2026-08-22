package com.mohdsohel.CloudNest.controller;

import com.mohdsohel.CloudNest.dto.PaymentDto;
import com.mohdsohel.CloudNest.dto.PaymentVerificationDto;
import com.mohdsohel.CloudNest.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentDto paymentDto){
        PaymentDto payment = paymentService.createOrder(paymentDto);

        if(payment.getSuccess()){
            return ResponseEntity.ok(payment);
        }
        else{
            return ResponseEntity.badRequest().body(payment);
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationDto request) {
        PaymentDto payment = paymentService.verifyPayment(request);

        if(payment.getSuccess()){
            return ResponseEntity.ok(payment);
        }
        else{
            return ResponseEntity.badRequest().body(payment);
        }
    }
}
