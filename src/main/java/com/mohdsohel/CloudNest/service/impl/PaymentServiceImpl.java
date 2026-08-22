package com.mohdsohel.CloudNest.service.impl;

import com.mohdsohel.CloudNest.document.PaymentTransaction;
import com.mohdsohel.CloudNest.document.ProfileDocument;
import com.mohdsohel.CloudNest.document.enums.PaymentStatus;
import com.mohdsohel.CloudNest.document.enums.Plans;
import com.mohdsohel.CloudNest.dto.PaymentDto;
import com.mohdsohel.CloudNest.dto.PaymentVerificationDto;
import com.mohdsohel.CloudNest.repository.PaymentRepository;
import com.mohdsohel.CloudNest.service.PaymentService;
import com.mohdsohel.CloudNest.service.ProfileService;
import com.mohdsohel.CloudNest.service.UserCreditsService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ProfileService profileService;
    private final PaymentRepository paymentRepository;
    private final UserCreditsService userCreditsService;
    private final ModelMapper modelMapper;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    @Override
    public PaymentDto createOrder(PaymentDto paymentDto) {
        try{
            ProfileDocument currentProfile = profileService.getProfile();
            String clerkId = currentProfile.getClerkId();
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpaySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", paymentDto.getAmount());
            orderRequest.put("currency", paymentDto.getCurrency());
            orderRequest.put("receipt", "order+"+System.currentTimeMillis());

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");

//            Create Pending Transaction
            PaymentTransaction paymentTransaction = PaymentTransaction.builder()
                    .clerkId(clerkId)
                    .orderId(orderId)
                    .planId(paymentDto.getPlanId())
                    .amount(paymentDto.getAmount())
                    .currency(paymentDto.getCurrency())
                    .status(PaymentStatus.PENDING)
                    .userEmail(currentProfile.getEmail())
                    .userName(currentProfile.getFirstName()+ " "+currentProfile.getLastName())
                    .paymentTransactionDate(LocalDateTime.now())
                    .build();

            paymentTransaction = paymentRepository.save(paymentTransaction);

            PaymentDto savedPayment =  modelMapper.map(paymentTransaction, PaymentDto.class);
            savedPayment.setOrderId(orderId);
            savedPayment.setSuccess(true);
            savedPayment.setMessage("Order Created Successfully.");

            return savedPayment;
        }catch (Exception ex){
            return PaymentDto.builder()
                    .success(false)
                    .message("Error creating Order: "+ ex.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentDto verifyPayment(PaymentVerificationDto request) {
        try{
            ProfileDocument currentProfile = profileService.getProfile();
            String clerkId = currentProfile.getClerkId();

            String data = request.getRazorpay_order_id()+ "|" + request.getRazorpay_payment_id();

            String generatedSignature = generateHmacSha256Signature(data,razorpaySecret);

            if(!generatedSignature.equals(request.getRazorpay_signature())){
                updateTransactionStatus(request.getRazorpay_order_id(), PaymentStatus.FAILED, request.getRazorpay_payment_id(),null);
                return PaymentDto.builder()
                        .success(false)
                        .message("Payment verification signature failed.")
                        .build();
            }

            int creditsToAdd=0;
            Plans plans = Plans.BASIC;

            switch (request.getPlanId()){
                case "premium":
                    plans = Plans.PREMIUM;
                    creditsToAdd=500;
                    break;
                case "ultimate":
                    plans = Plans.ULTIMATE;
                    creditsToAdd=5000;
            }

            if(creditsToAdd > 0){
                userCreditsService.addCredits(clerkId, creditsToAdd, plans);
                updateTransactionStatus(request.getRazorpay_order_id(), PaymentStatus.SUCCESS, request.getRazorpay_payment_id(), creditsToAdd);

                return PaymentDto.builder()
                        .success(true)
                        .message("Payment verified and credits added successfully.")
                        .credits(userCreditsService.getUserCredits(clerkId).getCredits())
                        .build();
            }
            else{
                updateTransactionStatus(request.getRazorpay_order_id(), PaymentStatus.FAILED, request.getRazorpay_payment_id(), null);
                return PaymentDto.builder()
                        .success(false)
                        .message("Invalid Plan selected.")
                        .build();
            }
        }catch (Exception ex){
            try{
                updateTransactionStatus(request.getRazorpay_order_id(), PaymentStatus.ERROR, request.getRazorpay_payment_id(), null);
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }

            return PaymentDto.builder()
                    .success(false)
                    .message("Error Verifying Payment: "+ ex.getMessage())
                    .build();
        }
    }

    private void updateTransactionStatus(String razorpayOrderId, PaymentStatus paymentStatus, String razorpayPaymentId, Integer creditsToAdd) {
        paymentRepository.findAll().stream()
                .filter(t-> t.getOrderId()!=null && t.getOrderId().equals(razorpayOrderId))
                .findFirst()
                .map(paymentTransaction -> {
                    paymentTransaction.setStatus(paymentStatus);
                    paymentTransaction.setPaymentId(razorpayPaymentId);

                    if(creditsToAdd !=null){
                        paymentTransaction.setCredits(creditsToAdd);
                    }

                    return paymentRepository.save(paymentTransaction);
                })
                .orElse(null);


    }

    private String generateHmacSha256Signature(String data, String razorpaySecret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    razorpaySecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );

            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(
                    data.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC SHA256 signature", e);
        }
    }
}
