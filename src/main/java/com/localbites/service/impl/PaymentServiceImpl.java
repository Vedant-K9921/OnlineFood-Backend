package com.localbites.service.impl;

import com.localbites.dto.payment.CreatePaymentRequest;
import com.localbites.dto.payment.RazorpayOrderResponse;
import com.localbites.dto.payment.VerifyPaymentRequest;
import com.localbites.entity.Order;
import com.localbites.enums.PaymentStatus;
import com.localbites.exception.ResourceNotFoundException;
import com.localbites.repository.OrderRepository;
import com.localbites.service.PaymentService;
import com.razorpay.OrderClient;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
        private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
public RazorpayOrderResponse createRazorpayOrder(
        Long userId,
        CreatePaymentRequest request
) {

    try {

        Order order =
                orderRepository.findById(
                        request.getOrderId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found"
                        ));

        if (!order.getUser().getId()
                .equals(userId)) {

            throw new RuntimeException(
                    "Unauthorized access"
            );
        }

        JSONObject options =
                new JSONObject();

        options.put(
                "amount",
                order.getTotalAmount()
                        .multiply(
                                java.math.BigDecimal.valueOf(100)
                        )
                        .longValue()
        );

        options.put(
                "currency",
                "INR"
        );

        options.put(
                "receipt",
                "order_" + order.getId()
        );

        com.razorpay.Order razorpayOrder =
                razorpayClient.orders.create(
                        options
                );

        String razorpayOrderId =
                razorpayOrder.get("id");

        order.setRazorpayOrderId(
                razorpayOrderId
        );

        orderRepository.save(order);

        return RazorpayOrderResponse.builder()
                .orderId(order.getId())
                .razorpayOrderId(
                        razorpayOrderId
                )
                .amount(
                        order.getTotalAmount()
                                .multiply(
                                        java.math.BigDecimal.valueOf(100)
                                )
                                .longValue()
                )
                .currency("INR")
                .key(keyId)
                .build();

    } catch (Exception ex) {

        throw new RuntimeException(
                "Failed to create Razorpay order",
                ex
        );
    }
}
@Override
public void verifyPayment(
        VerifyPaymentRequest request
) {

    Order order =
            orderRepository.findById(
                    request.getOrderId()
            )
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Order not found"
                    ));

    String payload =
            request.getRazorpayOrderId()
            + "|"
            + request.getRazorpayPaymentId();

    String generatedSignature =
            generateSignature(
                    payload,
                    keySecret
            );

    if (!generatedSignature.equals(
            request.getRazorpaySignature()
    )) {

        throw new RuntimeException(
                "Invalid payment signature"
        );
    }

    order.setPaymentStatus(
            PaymentStatus.PAID
    );

    orderRepository.save(order);
}
private String generateSignature(
        String data,
        String secret
) {

    try {

        Mac sha256Hmac =
                Mac.getInstance(
                        "HmacSHA256"
                );

        SecretKeySpec secretKey =
                new SecretKeySpec(
                        secret.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        "HmacSHA256"
                );

        sha256Hmac.init(secretKey);

        byte[] hash =
                sha256Hmac.doFinal(
                        data.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        StringBuilder result =
                new StringBuilder();

        for (byte b : hash) {

            result.append(
                    String.format(
                            "%02x",
                            b
                    )
            );
        }

        return result.toString();

    } catch (Exception ex) {

        throw new RuntimeException(
                ex
        );
    }
}
}