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

        // 1. Проверяем тип события
        String eventType = (String) notification.get("event");

        // 2. Достаем объект платежа
        Map<String, Object> payment = (Map<String, Object>) notification.get("object");

        // 3. Извлекаем ключевые данные
        String paymentId = (String) payment.get("id");
        String status = (String) payment.get("status");
        boolean paid = (Boolean) payment.get("paid");

        // 4. Обрабатываем метаданные (если есть)
        Map<String, Object> metadata = (Map<String, Object>) payment.get("metadata");
        Long orderId = metadata != null ? Long.valueOf(metadata.get("orderId").toString()) : null;


        // 5. Логика обработки
        if ("payment.succeeded".equals(eventType)) {
            orderService.confirmPayment(orderId);
        }


        return ResponseEntity.ok().build();
    }

}