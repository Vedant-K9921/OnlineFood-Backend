package com.localbites.service;

import com.localbites.dto.payment.CreatePaymentRequest;
import com.localbites.dto.payment.RazorpayOrderResponse;
import com.localbites.dto.payment.VerifyPaymentRequest;

public interface PaymentService {

    RazorpayOrderResponse createRazorpayOrder(
            Long userId,
            CreatePaymentRequest request
    );

    void verifyPayment(
            VerifyPaymentRequest request
    );
}