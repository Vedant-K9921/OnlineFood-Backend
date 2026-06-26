package com.localbites.controller;

import com.localbites.dto.payment.CreatePaymentRequest;
import com.localbites.dto.payment.RazorpayOrderResponse;
import com.localbites.dto.payment.VerifyPaymentRequest;
import com.localbites.response.ApiResponse;
import com.localbites.security.CustomUserDetails;
import com.localbites.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ApiResponse<RazorpayOrderResponse> createOrder(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            CreatePaymentRequest request
    ) {

        return ApiResponse.success(
                "Razorpay order created successfully",
                paymentService.createRazorpayOrder(
                        userDetails.getUserId(),
                        request
                )
        );
    }

    @PostMapping("/verify")
    public ApiResponse<String> verifyPayment(

            @Valid
            @RequestBody
            VerifyPaymentRequest request
    ) {

        paymentService.verifyPayment(request);

        return ApiResponse.success(
                "Payment verified successfully",
                "SUCCESS"
        );
    }

    @PostMapping("/webhook")
public ApiResponse<String> webhook(
        @RequestBody String payload
) {

    System.out.println(
            "Razorpay Webhook Received:"
    );

    System.out.println(payload);

    return ApiResponse.success(
            "Webhook received",
            "OK"
    );
}

}