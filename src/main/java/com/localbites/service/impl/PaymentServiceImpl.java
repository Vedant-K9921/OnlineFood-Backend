package com.localbites.service.impl;

import com.localbites.dto.payment.CreatePaymentRequest;
import com.localbites.dto.payment.RazorpayOrderResponse;
import com.localbites.dto.payment.VerifyPaymentRequest;
import com.localbites.entity.Order;
import com.localbites.enums.PaymentStatus;
import com.localbites.exception.BadRequestException;
import com.localbites.exception.ResourceNotFoundException;
import com.localbites.exception.UnauthorizedException;
import com.localbites.repository.OrderRepository;
import com.localbites.service.PaymentService;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;

    @Value("${razorpay.key.id}") private String keyId;
    @Value("${razorpay.key.secret}") private String keySecret;
    @Value("${razorpay.webhook.secret:}") private String webhookSecret;

    @Override
    public RazorpayOrderResponse createRazorpayOrder(Long userId, CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        verifyCustomer(order, userId);
        if (order.getPaymentStatus() == PaymentStatus.PAID) throw new BadRequestException("Order has already been paid");
        if (order.getTotalAmount() == null || order.getTotalAmount().signum() <= 0) throw new BadRequestException("Invalid order amount");
        try {
            if (order.getRazorpayOrderId() != null && !order.getRazorpayOrderId().isBlank()) return response(order, order.getRazorpayOrderId());
            long amountPaise = order.getTotalAmount().movePointRight(2).longValueExact();
            JSONObject options = new JSONObject();
            options.put("amount", amountPaise);
            options.put("currency", "INR");
            options.put("receipt", "order_" + order.getId());
            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);
            String razorpayOrderId = razorpayOrder.get("id");
            if (razorpayOrderId == null || razorpayOrderId.isBlank()) throw new BadRequestException("Razorpay did not return an order id");
            order.setRazorpayOrderId(razorpayOrderId);
            orderRepository.save(order);
            return response(order, razorpayOrderId);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BadRequestException("Failed to create Razorpay order");
        }
    }

    @Override
    public void verifyPayment(Long userId, VerifyPaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        verifyCustomer(order, userId);
        if (order.getPaymentStatus() == PaymentStatus.PAID) return;
        if (order.getRazorpayOrderId() == null || !order.getRazorpayOrderId().equals(request.getRazorpayOrderId())) throw new BadRequestException("Razorpay order does not match local order");

        String expectedSignature = generateSignature(request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId(), keySecret);
        if (!constantTimeEquals(expectedSignature, request.getRazorpaySignature())) throw new BadRequestException("Invalid payment signature");

        try {
            JSONObject payment = new JSONObject(razorpayClient.payments.fetch(request.getRazorpayPaymentId()).toString());
            long expectedPaise = order.getTotalAmount().movePointRight(2).longValueExact();
            if (!order.getRazorpayOrderId().equals(payment.optString("order_id", ""))
                    || payment.optLong("amount", -1L) != expectedPaise
                    || !"INR".equalsIgnoreCase(payment.optString("currency", ""))
                    || !"captured".equalsIgnoreCase(payment.optString("status", ""))) {
                throw new BadRequestException("Payment details do not match the order");
            }
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BadRequestException("Unable to verify payment with Razorpay");
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        orderRepository.save(order);
    }

    @Override
    public void verifyWebhook(String payload, String signature) {
        if (webhookSecret == null || webhookSecret.isBlank()) throw new BadRequestException("Payment webhook secret is not configured");
        if (!constantTimeEquals(generateSignature(payload, webhookSecret), signature)) throw new BadRequestException("Invalid webhook signature");
        try {
            JSONObject root = new JSONObject(payload);
            JSONObject payment = root.optJSONObject("payload");
            payment = payment == null ? null : payment.optJSONObject("payment");
            payment = payment == null ? null : payment.optJSONObject("entity");
            if (payment == null) throw new BadRequestException("Invalid webhook payload");
            String razorpayOrderId = payment.optString("order_id", "");
            String status = payment.optString("status", "");
            if (razorpayOrderId.isBlank()) return;
            orderRepository.findByRazorpayOrderId(razorpayOrderId).ifPresent(order -> {
                if ("captured".equalsIgnoreCase(status)) order.setPaymentStatus(PaymentStatus.PAID);
                else if ("failed".equalsIgnoreCase(status)) order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(order);
            });
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BadRequestException("Invalid webhook payload");
        }
    }

    private RazorpayOrderResponse response(Order order, String razorpayOrderId) {
        return RazorpayOrderResponse.builder().orderId(order.getId()).razorpayOrderId(razorpayOrderId)
                .amount(order.getTotalAmount().movePointRight(2).longValueExact()).currency("INR").key(keyId).build();
    }

    private void verifyCustomer(Order order, Long userId) {
        if (!order.getUser().getId().equals(userId)) throw new UnauthorizedException("You do not own this order");
    }

    private String generateSignature(String data, String secret) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(hash.length * 2);
            for (byte b : hash) result.append(String.format("%02x", b));
            return result.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to generate payment signature", ex);
        }
    }

    private boolean constantTimeEquals(String left, String right) {
        if (left == null || right == null) return false;
        return MessageDigest.isEqual(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }
}
