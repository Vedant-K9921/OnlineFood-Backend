package com.localbites.controller;

import com.localbites.dto.payment.CreatePaymentRequest;
import com.localbites.dto.payment.RazorpayOrderResponse;
import com.localbites.dto.payment.VerifyPaymentRequest;
import com.localbites.response.ApiResponse;
import com.localbites.security.CustomUserDetails;
import com.localbites.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<RazorpayOrderResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePaymentRequest request) {
        return ApiResponse.success(
                "Razorpay order created successfully",
                paymentService.createRazorpayOrder(userDetails.getUserId(), request));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String> verifyPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody VerifyPaymentRequest request) {
        paymentService.verifyPayment(userDetails.getUserId(), request);
        return ApiResponse.success("Payment verified successfully", "SUCCESS");
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> webhook(
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature,
            @RequestBody String payload) {
        paymentService.verifyWebhook(payload, signature);
        return ResponseEntity.ok(ApiResponse.success("Webhook received", "OK"));
    }
}
