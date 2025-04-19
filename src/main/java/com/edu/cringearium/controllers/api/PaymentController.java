package com.edu.cringearium.controllers.api;


import com.edu.cringearium.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final OrderService orderService;

    public PaymentController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> notification) {

        // Проверяем тип события
        String eventType = (String) notification.get("event");

        // Достаем объект платежа
        Map<String, Object> payment = (Map<String, Object>) notification.get("object");

        // Извлекаем ключевые данные
        String paymentId = (String) payment.get("id");
        String status = (String) payment.get("status");
        boolean paid = (Boolean) payment.get("paid");

        // Обрабатываем метаданные (если есть)
        Map<String, Object> metadata = (Map<String, Object>) payment.get("metadata");
        Long orderId = metadata != null ? Long.valueOf(metadata.get("orderId").toString()) : null;


        // Логика обработки
        if ("payment.succeeded".equals(eventType)) {
            orderService.confirmPayment(orderId);
        }


        return ResponseEntity.ok().build();
    }

}